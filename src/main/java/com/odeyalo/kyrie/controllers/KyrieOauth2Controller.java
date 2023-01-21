package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.controllers.support.AdvancedModelAttribute;
import com.odeyalo.kyrie.controllers.support.AuthorizationRequestValidator;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.AuthenticationResult;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.authorization.support.AuthorizationRequestContext;
import com.odeyalo.kyrie.core.authorization.support.AuthorizationRequestContextHolder;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandler;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationServiceFactory;
import com.odeyalo.kyrie.core.oauth2.support.grant.AuthorizationGrantTypeResolver;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.sso.RememberedLoggedUserAccountsContainer;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.dto.ApiErrorMessage;
import com.odeyalo.kyrie.dto.LoginDTO;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;
import com.odeyalo.kyrie.exceptions.RedirectUriAwareOauth2Exception;
import com.odeyalo.kyrie.support.html.DefaultTemplateResolver;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main controller that provides '/authorize' and '/login' endpoints .
 * <p>
 * The KyrieOauth2Controller is used to create {@link AuthorizationRequest} and store it in Session attributes,
 * used to login an user and return an oauth2 token to client.
 * </p>
 *
 * @version 1.0
 * @see com.odeyalo.kyrie.config.configuration.KyrieOauth2ServerEndpointsMappingConfiguration
 */
@SessionAttributes(value = {KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME})
public class KyrieOauth2Controller {

    private final Oauth2UserAuthenticationService oauth2UserAuthenticationService;
    private final Oauth2FlowHandlerFactory oauth2FlowHandlerFactory;
    private final AuthorizationGrantTypeResolver grantTypeResolver;
    private final RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory;
    private final AuthorizationRequestValidator validator;
    private final TemplateResolver templateResolver;

    @Autowired
    private RememberMeService rememberMeService;

    public static final String AUTHORIZATION_REQUEST_ATTRIBUTE_NAME = "authorizationRequest";
    public static final String WRONG_CREDENTIALS_ERROR_NAME = "wrong_credentials";
    public static final String MISSING_AUTHORIZATION_REQUEST_ERROR_NAME = "missing_authorization_request";
    public static final String UNSUPPORTED_GRANT_TYPE_ERROR_NAME = "unsupported_grant_type";

    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2Controller.class);

    public KyrieOauth2Controller(Oauth2UserAuthenticationService oauth2UserAuthenticationService, Oauth2FlowHandlerFactory oauth2FlowHandlerFactory,
                                 AuthorizationGrantTypeResolver grantTypeResolver,
                                 RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory,
                                 AuthorizationRequestValidator validator, TemplateResolver templateResolver) {
        this.oauth2UserAuthenticationService = oauth2UserAuthenticationService;
        this.oauth2FlowHandlerFactory = oauth2FlowHandlerFactory;
        this.grantTypeResolver = grantTypeResolver;
        this.redirectUrlCreationServiceFactory = redirectUrlCreationServiceFactory;
        this.validator = validator;
        this.templateResolver = templateResolver;
    }

    @ModelAttribute(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME)
    public Map<String, Object> authorizationRequest() {
        return new ConcurrentHashMap<>(256);
    }

    /**
     * '/authorize' endpoint for only GET HTTP requests.
     * Used to validate and store valid {@link AuthorizationRequest} in Session Attributes.
     * It also stores the valid AuthorizationRequest in {@link AuthorizationRequestContextHolder} for LOCAL thread only.
     * <p>
     * The endpoint response is html template.
     *
     * @param clientId                  - client application id that wants to access user's data
     * @param responseTypes             - oauth2 response types that must be returned to client.
     * @param scopes                    - scopes that client application request. Unknown scopes will be ignored
     * @param redirectUrl               - redirect url to redirect after successful or failed user authentication. If url is malformed then 400 BAD REQUEST will be returned to end user.
     * @param state                     - optional parameter. If state was presented, then state will be returned in request parameters from redirect_uri parameter.
     * @param authorizationRequestStore - session store
     * @return - {@link ModelAndView} resolved by {@link TemplateResolver}
     * @see AuthorizationRequest
     * @see AuthorizationRequestContextHolder
     * @see javax.servlet.http.HttpSession
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-3.1.1">Oauth2 Response Type</a>
     * @see <a href="https://www.oauth.com/oauth2-servers/authorization/the-authorization-request/">The Authorization Request</a>
     */
    @GetMapping(value = "/authorize")
    public ModelAndView authorization(
            @RequestParam("client_id") String clientId,
            @RequestParam("response_type") Oauth2ResponseType[] responseTypes,
            @RequestParam("scope") String[] scopes,
            @RequestParam(name = "redirect_uri") String redirectUrl,
            @RequestParam(name = "state", required = false) String state,
            @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> authorizationRequestStore) {
        HttpServletRequest currentReq = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

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
            // The AuthorizationRequest is not valid, throw exception based on validation result
            throw resolveException(redirectUrl, validationResult);
        }
        authorizationRequestStore.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        AuthorizationRequestContextHolder.setContext(new AuthorizationRequestContext(request));

        RememberedLoggedUserAccountsContainer accountsContainer = rememberMeService.autoLogin(currentReq);

        if (accountsContainer.isEmpty()) {
            return templateResolver.getTemplate(DefaultTemplateResolver.LOGIN_TEMPLATE_TYPE);
        }

        this.logger.info("The LoggedUserAccountsContainer returns not empty list of remembered account. Using USER_ALREADY_LOGGED_IN_TEMPLATE_TYPE template");
        Model model = new ExtendedModelMap();
        model.addAttribute("users", accountsContainer.getUsers());
        return templateResolver.getTemplate(DefaultTemplateResolver.USER_ALREADY_LOGGED_IN_TEMPLATE_TYPE, model);
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginUserFromSessionAndDoGrantTypeProcessing(HttpServletRequest request,
                                                                          @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> sessionStore,
                                                                          SessionStatus status) {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) sessionStore.get(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME);

        if (authorizationRequest == null) {
            ApiErrorMessage errorMessage = new ApiErrorMessage(MISSING_AUTHORIZATION_REQUEST_ERROR_NAME, "Session attribute does not found and request cannot be processed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        RememberedLoggedUserAccountsContainer accountsContainer = rememberMeService.autoLogin(request);

        if (accountsContainer.isEmpty()) {
            return ResponseEntity.badRequest().body("The session does not contain user. Use POST request to authenticate without session");
        }

        List<Oauth2User> users = accountsContainer.getUsers();
        Oauth2User oauth2User = users.get(0);

        String redirectUrl = doGrantTypeProcessing(authorizationRequest, oauth2User, status);

        if (redirectUrl == null) {
            ApiErrorMessage message = new ApiErrorMessage(UNSUPPORTED_GRANT_TYPE_ERROR_NAME, "Kyrie does not support: " + authorizationRequest.getGrantType());
            return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
    }
    /**
     * The '/login' endpoint for only POST HTTP request with ONLY application/json content-type.
     * <p>Possible Http response supported by endpoint:</p>
     * <ul>
     *     <li>HTTP 302 REDIRECT with values based on {@link AuthorizationRequest} that was stored in sessionStore if login was success</li>
     *     <li>HTTP 400 BAD REQUEST if session store does not contain {@link KyrieOauth2Controller#AUTHORIZATION_REQUEST_ATTRIBUTE_NAME} or if AuthorizationRequest is malformed</li>
     *     <li>HTTP 500 SERVER ERROR if error was occurred and no exception handler was found.</li>
     * </ul>
     *
     * @param dto          - dto that contains user's username and password
     * @param sessionStore - store that contains the values for the given session
     * @param status       - current session status. After successful login the session will be closed.
     * @return - ResponseEntity with redirect or error, see above for more info.
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginCheckAndGrantTypeProcessingUsingJson(@RequestBody LoginDTO dto,
                                                                       @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> sessionStore,
                                                                       SessionStatus status) {
        return doLoginAndGrantTypeProcessing(dto, sessionStore, status);
    }

    /**
     * The '/login' endpoint for only POST HTTP request with multipart/form-data and application/x-www-form-urlencoded content-types.
     * <ul>
     *     <li>HTTP 302 REDIRECT with values based on {@link AuthorizationRequest} that was stored in sessionStore if login was success</li>
     *     <li>HTTP 400 BAD REQUEST if session store does not contain {@link KyrieOauth2Controller#AUTHORIZATION_REQUEST_ATTRIBUTE_NAME} or if AuthorizationRequest is malformed</li>
     *     <li>HTTP 500 SERVER ERROR if error was occurred and no exception handler was found.</li>
     * </ul>
     *
     * @param dto          - dto that contains user's username and password
     * @param sessionStore - store that contains the values for the given session
     * @param status       - current session status. After successful login the session will be closed.
     * @return - ResponseEntity with redirect or error, see above for more info.
     */
    @PostMapping(value = "/login", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> loginCheckAndGrantTypeProcessingUsingFormData(@AdvancedModelAttribute LoginDTO dto,
                                                                           @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> sessionStore,
                                                                           SessionStatus status) {
        return doLoginAndGrantTypeProcessing(dto, sessionStore, status);
    }

    private ResponseEntity<?> doLoginAndGrantTypeProcessing(LoginDTO dto, Map<String, Object> sessionStore, SessionStatus status) {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) sessionStore.get(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME);

        if (authorizationRequest == null) {
            ApiErrorMessage errorMessage = new ApiErrorMessage(MISSING_AUTHORIZATION_REQUEST_ERROR_NAME, "Session attribute does not found and request cannot be processed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        logger.info("Auth request: {}", authorizationRequest);


        AuthenticationResult result = oauth2UserAuthenticationService.authenticate(new Oauth2UserAuthenticationInfo(dto.getUsername(), dto.getPassword()));

        if (result == null || !result.isSuccess()) {
            ApiErrorMessage errorMessage = new ApiErrorMessage(WRONG_CREDENTIALS_ERROR_NAME, "User credentials are wrong and login can't be performed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }

        Oauth2User user = result.getUser();

        String redirectUrl = doGrantTypeProcessing(authorizationRequest, user, status);

        if (redirectUrl == null) {
            ApiErrorMessage message = new ApiErrorMessage(UNSUPPORTED_GRANT_TYPE_ERROR_NAME, "Kyrie does not support: " + authorizationRequest.getGrantType());
            return ResponseEntity.badRequest().body(message);
        }

        logger.info("Redirecting to: {}", redirectUrl);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        HttpServletRequest request = requestAttributes.getRequest();

        rememberMeService.rememberMe(user, request, response);

        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
    }

    private String doGrantTypeProcessing(AuthorizationRequest authorizationRequest, Oauth2User user, SessionStatus status) {
        Oauth2FlowHandler oauth2FlowHandler = oauth2FlowHandlerFactory.getOauth2FlowHandler(authorizationRequest);
        if (oauth2FlowHandler == null) {
            return null;
        }
        Oauth2Token token = oauth2FlowHandler.handleFlow(authorizationRequest, user);
        String redirectUrl = redirectUrlCreationServiceFactory.getRedirectUrlCreationService(authorizationRequest).createRedirectUrl(authorizationRequest, token);
        // No need to clear the sessionStore, if session is completed, then session store will be automatically cleared by DefaultSessionAttributeStore
        status.setComplete();
        return redirectUrl;
    }

    private RuntimeException resolveException(String redirectUrl, Oauth2ValidationResult validationResult) {
        if (Oauth2ErrorType.INVALID_REDIRECT_URI.equals(validationResult.getErrorType())) {
            return new Oauth2Exception(
                    String.format("Failed to initialize Authorization request. Reason: %s", validationResult.getMessage()),
                    validationResult.getMessage(), validationResult.getErrorType());
        }
        return new RedirectUriAwareOauth2Exception(String.format("Failed to initialize Authorization request. Reason: %s", validationResult.getMessage()),
                validationResult.getMessage(), redirectUrl, validationResult.getErrorType());
    }
}
