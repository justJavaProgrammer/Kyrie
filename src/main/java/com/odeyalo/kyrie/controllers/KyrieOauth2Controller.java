package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.controllers.support.AuthorizationRequestValidator;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.AuthenticationResult;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandler;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationServiceFactory;
import com.odeyalo.kyrie.core.oauth2.support.grant.AuthorizationGrantTypeResolver;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.dto.LoginDTO;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;
import com.odeyalo.kyrie.exceptions.RedirectUriAwareOauth2Exception;
import com.odeyalo.kyrie.support.cookie.CookieUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//todo: Write test for this controller
@Controller
@RequestMapping("/oauth2/")
@SessionAttributes(value = {KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME})
@Log4j2
public class KyrieOauth2Controller {
    public static final String AUTHORIZATION_REQUEST_ATTRIBUTE_NAME = "authorizationRequest";
    private final Oauth2UserAuthenticationService oauth2UserAuthenticationService;
    @Autowired
    private Oauth2FlowHandlerFactory handlerFactory;
    @Autowired
    private AuthorizationGrantTypeResolver grantTypeResolver;
    @Autowired
    private RedirectUrlCreationServiceFactory factory;
    @Autowired
    private AuthorizationRequestValidator validator;

    public KyrieOauth2Controller(Oauth2UserAuthenticationService oauth2UserAuthenticationService) {
        this.oauth2UserAuthenticationService = oauth2UserAuthenticationService;
    }

    @ModelAttribute(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME)
    public Map<String, Object> authorizationRequest() {
        return new ConcurrentHashMap<>(256);
    }

    //todo add logic to retrieve info from session
    @GetMapping(value = "/authorize")
    public String authorization(
            @RequestParam("client_id") String clientId,
            @RequestParam("response_type") Oauth2ResponseType[] responseTypes,
            @RequestParam("scope") String[] scopes,
            @RequestParam(name = "redirect_uri") String redirectUrl,
            @RequestParam(name = "state", required = false) String state,
            @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> authorizationRequestStore,
            HttpServletResponse res) {

        AuthorizationGrantType grantType = grantTypeResolver.resolveGrantType(responseTypes);
        AuthorizationRequest request = AuthorizationRequest.builder().clientId(clientId)
                .responseTypes(responseTypes)
                .grantType(grantType)
                .redirectUrl(redirectUrl)
                .scopes(scopes)
                .state(state)
                .build();

        Oauth2ValidationResult validationResult = validator.validateAuthorizationRequest(request);
        if (!validationResult.isSuccess()) {
            resolveExceptionAndThrow(redirectUrl, validationResult);
        }
        authorizationRequestStore.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);
        String serialize = CookieUtils.serialize(Collections.singletonList(new LoginDTO("aboba", "aboba")));
        CookieUtils.addCookie(res, "ACCOUNT_CHOOSER", serialize, 3600);
        return "login";
    }

    /**
     * Login an user AND return the access token to a client
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginCheckAndGrantTypeProcessingUsingJson(@RequestBody LoginDTO dto,
                                                                       @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> model,
                                                                       SessionStatus status
    ) {
        log.info("Received model: {}", model);
        log.info("session status: {}", status);
        return doLoginAndGrantTypeProcessing(dto, model, status);
    }

    @PostMapping(value = "/login", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> loginCheckAndGrantTypeProcessingUsingFormData(@ModelAttribute LoginDTO dto,
                                                                           @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> model,
                                                                           SessionStatus status
    ) {
        return doLoginAndGrantTypeProcessing(dto, model, status);
    }

    private ResponseEntity<Object> doLoginAndGrantTypeProcessing(LoginDTO dto, Map<String, Object> model, SessionStatus status) {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME);
        if (authorizationRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session attribute is not found and request cannot be processed");
        }
        log.info("Auth request: {}", authorizationRequest);

        log.info("Using {} as authentication service", oauth2UserAuthenticationService);
        AuthenticationResult result = oauth2UserAuthenticationService.authenticate(new Oauth2UserAuthenticationInfo(dto.getUsername(), dto.getPassword()));
        if (result == null || !result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong user credentials");
        }

        Oauth2User user = result.getUser();

        Oauth2FlowHandler oauth2FlowHandler = handlerFactory.getOauth2FlowHandler(authorizationRequest);
        if (oauth2FlowHandler == null) {
            return ResponseEntity.badRequest().body("Wrong authorization request");
        }
        Oauth2Token token = oauth2FlowHandler.handleFlow(authorizationRequest, user);
        String redirectUrl = factory.getRedirectUrlCreationService(authorizationRequest).createRedirectUrl(authorizationRequest, token);
        model.clear();
        status.setComplete();
        log.info("Redirecting to: {}", redirectUrl);
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
    }


    private void resolveExceptionAndThrow(String redirectUrl, Oauth2ValidationResult validationResult) {
        if (validationResult.getErrorType().equals(Oauth2ErrorType.INVALID_REDIRECT_URI)) {
            throw new Oauth2Exception(
                    String.format("Failed to initialize Authorization request. Reason: %s", validationResult.getMessage()),
                    validationResult.getMessage(), validationResult.getErrorType());
        }
        throw new RedirectUriAwareOauth2Exception(String.format("Failed to initialize Authorization request. Reason: %s", validationResult.getMessage()),
                validationResult.getMessage(), redirectUrl, validationResult.getErrorType());
    }
}
