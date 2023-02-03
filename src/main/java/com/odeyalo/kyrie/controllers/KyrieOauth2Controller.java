package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.controllers.support.AdvancedModelAttribute;
import com.odeyalo.kyrie.controllers.support.validation.ValidAuthorizationRequest;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.support.AuthorizationRequestContext;
import com.odeyalo.kyrie.core.authorization.support.AuthorizationRequestContextHolder;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandler;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.prompt.PromptHandler;
import com.odeyalo.kyrie.core.oauth2.prompt.PromptHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationServiceFactory;
import com.odeyalo.kyrie.core.oauth2.support.consent.ConsentPageHandler;
import com.odeyalo.kyrie.core.oauth2.support.grant.RedirectableAuthenticationGrantHandlerFacade;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.sso.RememberedLoggedUserAccountsContainer;
import com.odeyalo.kyrie.core.support.web.TemporaryRequestAttributesRepository;
import com.odeyalo.kyrie.dto.ApiErrorMessage;
import com.odeyalo.kyrie.dto.LoginDTO;
import com.odeyalo.kyrie.exceptions.UnsupportedPromptTypeException;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.odeyalo.kyrie.core.oauth2.prompt.CombinedPromptHandler.COMBINED_PROMPT_TYPE_NAME;

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

    private final Oauth2FlowHandlerFactory oauth2FlowHandlerFactory;
    private final RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory;
    private final RememberMeService rememberMeService;
    private final PromptHandlerFactory promptHandlerFactory;
    private final TemporaryRequestAttributesRepository requestAttributesRepository;
    private final RedirectableAuthenticationGrantHandlerFacade redirectableAuthenticationGrantHandlerFacade;
    private final ConsentPageHandler consentPageHandler;

    public static final String AUTHORIZATION_REQUEST_ATTRIBUTE_NAME = "authorizationRequest";
    public static final String WRONG_CREDENTIALS_ERROR_NAME = "wrong_credentials";
    public static final String MISSING_AUTHORIZATION_REQUEST_ERROR_NAME = "missing_authorization_request";
    public static final String UNSUPPORTED_GRANT_TYPE_ERROR_NAME = "unsupported_grant_type";

    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2Controller.class);


    public KyrieOauth2Controller(Oauth2FlowHandlerFactory oauth2FlowHandlerFactory,
                                 RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory,
                                 RememberMeService rememberMeService,
                                 PromptHandlerFactory promptHandlerFactory,
                                 TemporaryRequestAttributesRepository requestAttributesRepository,
                                 RedirectableAuthenticationGrantHandlerFacade redirectableAuthenticationGrantHandlerFacade,
                                 ConsentPageHandler consentPageHandler) {
        this.oauth2FlowHandlerFactory = oauth2FlowHandlerFactory;
        this.redirectUrlCreationServiceFactory = redirectUrlCreationServiceFactory;
        this.rememberMeService = rememberMeService;
        this.promptHandlerFactory = promptHandlerFactory;
        this.requestAttributesRepository = requestAttributesRepository;
        this.redirectableAuthenticationGrantHandlerFacade = redirectableAuthenticationGrantHandlerFacade;
        this.consentPageHandler = consentPageHandler;
    }

    @ModelAttribute(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME)
    public Map<String, Object> authorizationRequest() {
        return new ConcurrentHashMap<>(256);
    }

    /**
     * '/authorize' endpoint for only GET HTTP requests.
     * The endpoint only put AuthorizationRequest in session store and return the html template to enter the credentials.
     * It also stores the valid AuthorizationRequest in {@link AuthorizationRequestContextHolder} for LOCAL thread only.
     * <p>
     * The endpoint response is html template.
     *
     * @param request                   - ready to use AuthorizationRequest that already passed all checks
     * @param authorizationRequestStore - session store
     * @return - {@link ModelAndView} resolved by {@link TemplateResolver}. Template that will be returned to user
     * @see AuthorizationRequest
     * @see AuthorizationRequestContextHolder
     * @see javax.servlet.http.HttpSession
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-3.1.1">Oauth2 Response Type</a>
     * @see <a href="https://www.oauth.com/oauth2-servers/authorization/the-authorization-request/">The Authorization Request</a>
     */
    @GetMapping(value = "/authorize")
    public ModelAndView authorization(@ValidAuthorizationRequest AuthorizationRequest request,
                                      @RequestParam(value = "prompt", defaultValue = COMBINED_PROMPT_TYPE_NAME) String promptType,
                                      @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> authorizationRequestStore) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletResponse response = attributes.getResponse();
        HttpServletRequest currentReq = attributes.getRequest();

        authorizationRequestStore.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        AuthorizationRequestContextHolder.setContext(new AuthorizationRequestContext(request));

        PromptHandler promptHandler = promptHandlerFactory.getHandler(promptType);
        if (promptHandler == null) {
            throw new UnsupportedPromptTypeException("The given prompt does not supported by Oauth Server.", request.getRedirectUrl());
        }
        // Delegate all job to PromptHandler
        return promptHandler.handlePrompt(new ExtendedModelMap(), currentReq, response);
    }

    @GetMapping("/consent")
    public ModelAndView consentPage(@ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> sessionStore, HttpServletRequest request) {
        Oauth2User user = requestAttributesRepository.get(request, Oauth2User.class);

        AuthorizationRequest authorizationRequest = (AuthorizationRequest) sessionStore.get(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME);

        return consentPageHandler.getConsentPage(user, authorizationRequest, request);
    }

    @PostMapping("/consent")
    public ResponseEntity<?> handleConsentSubmit(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 @ModelAttribute(KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) Map<String, Object> sessionStore) {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) sessionStore.get(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME);
        consentPageHandler.handleSubmit(authorizationRequest, request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginUserFromRememberMeAndDoGrantTypeProcessing(HttpServletRequest request,
                                                                             @RequestParam(name = "user_id") String userId,
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

        Oauth2User oauth2User = accountsContainer.get(userId);

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

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        HttpServletRequest request = requestAttributes.getRequest();

        RedirectableAuthenticationGrantHandlerFacade.HandleResult result = redirectableAuthenticationGrantHandlerFacade.handleGrant(new Oauth2UserAuthenticationInfo(dto.getUsername(), dto.getPassword()), authorizationRequest, request, response);

        ResponseEntity<?> responseEntity = ResponseEntity.internalServerError().body("Request processing cannot be performed and error is unknown");

        if (result.isSuccess()) {
            String redirectUri = result.getRedirectUri();
            responseEntity = ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUri).build();
        }

        if (RedirectableAuthenticationGrantHandlerFacade.HandleResult.WRONG_USER_CREDENTIALS_HANDLE_RESULT.equals(result)) {
            ApiErrorMessage errorMessage = new ApiErrorMessage(WRONG_CREDENTIALS_ERROR_NAME, "User credentials are wrong and login can't be performed");
            responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }

        if (RedirectableAuthenticationGrantHandlerFacade.HandleResult.UNSUPPORTED_GRANT_TYPE_HANDLE_RESULT.equals(result)) {
            ApiErrorMessage message = new ApiErrorMessage(UNSUPPORTED_GRANT_TYPE_ERROR_NAME, "Kyrie does not support: " + authorizationRequest.getGrantType());
            // TODO: Should be redirect instead of HTTP 400
            responseEntity = ResponseEntity.badRequest().body(message);
        }

        if (result.shouldCloseSession()) {
            requestAttributesRepository.clear(request);
            status.setComplete();
        }

        return responseEntity;
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
}
