package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.KyrieOauth2Configurer;
import com.odeyalo.kyrie.config.KyrieOauth2ConfigurerComposite;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerViewRegistry;
import com.odeyalo.kyrie.controllers.KyrieOauth2Controller;
import com.odeyalo.kyrie.controllers.TokenController;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.prompt.PromptHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationServiceFactory;
import com.odeyalo.kyrie.core.oauth2.support.consent.ConsentPageHandler;
import com.odeyalo.kyrie.core.oauth2.support.grant.RedirectableAuthenticationGrantHandlerFacade;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessTokenManager;
import com.odeyalo.kyrie.core.oauth2.tokens.facade.AccessTokenGranterStrategyFacadeWrapper;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.support.web.TemporaryRequestAttributesRepository;
import com.odeyalo.kyrie.dto.LoginDTO;
import com.odeyalo.kyrie.support.html.DefaultTemplateResolver;
import com.odeyalo.kyrie.support.html.ModelEnhancerPostProcessor;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Configuration that uses to add Kyrie endpoints to {@link org.springframework.web.servlet.DispatcherServlet}
 * using {@link WebMvcRegistrations}
 * </p>
 * <p>
 * The configuration is customizable, that means endpoints can be overridden using {@link KyrieOauth2Configurer}
 * </p>
 *
 * @version 1.0
 * @see Oauth2ServerEndpointsConfigurer
 */
@EnableWebMvc
public class KyrieOauth2ServerEndpointsMappingConfiguration {

    public static final String DEFAULT_LOGIN_TEMPLATE_NAME = "login.html";
    public static final String DEFAULT_USER_LOGGED_TEMPLATE_NAME = "user-logged.html";
    public static final String DEFAULT_CONSENT_TEMPLATE_NAME = "consent-login.html";
    private final KyrieOauth2ConfigurerComposite configurer = new KyrieOauth2ConfigurerComposite();
    private final ViewResolver viewResolver;
    private Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo info;

    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2ServerEndpointsMappingConfiguration.class);


    public KyrieOauth2ServerEndpointsMappingConfiguration(@Qualifier("thymeleafViewResolver") ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    @Autowired(required = false)
    public void setConfigurers(List<KyrieOauth2Configurer> configurers) {
        this.configurer.addAll(configurers);
    }

    /**
     * Secondary constructor that used to initialize Oauth2ServerEndpointsConfigurer and create Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo
     *
     * @see Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo
     * @see Oauth2ServerEndpointsConfigurer
     */
    @PostConstruct
    public void configurerInitialize() {

    }

    @Bean
    public Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo oauth2ServerEndpointsInfo() {
        Oauth2ServerEndpointsConfigurer configurer = new Oauth2ServerEndpointsConfigurer();
        this.configurer.configureEndpoints(configurer);
        info = configurer.buildOauth2ServerEndpointsInfo();
        return info;
    }

    /**
     * Registry the endpoints in DispatcherServlet using WebMvcRegistrations bean.
     * @param kyrieOauth2Controller - KyrieOauth2Controller bean
     * @param tokenController - TokenController bean
     * @param mapping - HandlerMapping where endpoints will be registered
     * @return - WebMvcRegistrations bean with registered default endpoints
     * @throws Exception - if any exception was occurred
     */
    @Bean
    public WebMvcRegistrations webMvcRegistrations(KyrieOauth2Controller kyrieOauth2Controller,
                                                   TokenController tokenController,
                                                   RequestMappingHandlerMapping mapping) throws Exception {

        registryAuthorizeEndpoint(kyrieOauth2Controller, mapping);

        registryLoginEndpointJson(kyrieOauth2Controller, mapping);

        registryLoginEndpointFormData(kyrieOauth2Controller, mapping);

        registryTokenEndpointJson(tokenController, mapping);

        registryGetConsentPage(kyrieOauth2Controller, mapping);

        registryHandleConsentPage(kyrieOauth2Controller, mapping);

        registryLoginUserFromSessionAndDoGrantTypeProcessing(kyrieOauth2Controller, mapping);

        registryTokenEndpointFormData(tokenController, mapping);

        registryTokenInfoEndpoint(tokenController, mapping);



        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return mapping;
            }
        };
    }

    @Bean
    public KyrieOauth2Controller kyrieOauth2Controller(Oauth2FlowHandlerFactory oauth2FlowHandlerFactory,
                                                       RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory,
                                                       RememberMeService rememberMeService,
                                                       PromptHandlerFactory promptHandlerFactory,
                                                       TemporaryRequestAttributesRepository requestAttributesRepository,
                                                       RedirectableAuthenticationGrantHandlerFacade redirectableAuthenticationGrantHandlerFacade,
                                                       ConsentPageHandler consentPageHandler) {
        return new KyrieOauth2Controller(oauth2FlowHandlerFactory,
                redirectUrlCreationServiceFactory, rememberMeService, promptHandlerFactory, requestAttributesRepository,
                redirectableAuthenticationGrantHandlerFacade, consentPageHandler);
    }

    /**
     * Registry {@link TemplateResolver} bean inside the Spring Container. Registry default templates that used by Kyrie.
     * It also configurers by {@link Oauth2ServerViewRegistry}.
     * @param processors - custom enhancers
     * @return - DefaultTemplateResolver with added views and enhancers
     * @throws Exception - if any exception occurred
     */
    @Bean
    @ConditionalOnMissingBean
    @Autowired(required = false)
    public TemplateResolver templateResolver(List<ModelEnhancerPostProcessor> processors) throws Exception {
        Oauth2ServerViewRegistry viewRegistry = new Oauth2ServerViewRegistry();

        configurer.configureTemplates(viewRegistry);

        Map<String, View> views = viewRegistry.getViews();
        View loginView = viewResolver.resolveViewName(DEFAULT_LOGIN_TEMPLATE_NAME, Locale.ENGLISH);
        View userLoggedView = viewResolver.resolveViewName(DEFAULT_USER_LOGGED_TEMPLATE_NAME, Locale.ENGLISH);
        View consentView = viewResolver.resolveViewName(DEFAULT_CONSENT_TEMPLATE_NAME, Locale.ENGLISH);

        views.putIfAbsent(DefaultTemplateResolver.LOGIN_TEMPLATE_TYPE, loginView);
        views.putIfAbsent(DefaultTemplateResolver.USER_ALREADY_LOGGED_IN_TEMPLATE_TYPE, userLoggedView);
        views.putIfAbsent(DefaultTemplateResolver.CONSENT_VIEW_TEMPLATE_TYPE, consentView);

        DefaultTemplateResolver defaultTemplateResolver = new DefaultTemplateResolver(processors);

        for (Map.Entry<String, View> entry : views.entrySet()) {
            String templateType = entry.getKey();
            View view = entry.getValue();
            defaultTemplateResolver.addTemplate(templateType, view);
        }
        return defaultTemplateResolver;
    }

    @Bean
    public TokenController tokenController(Oauth2AccessTokenManager accessTokenManager, AccessTokenGranterStrategyFacadeWrapper wrapper) {
        return new TokenController(accessTokenManager, wrapper);
    }

    private void registryTokenInfoEndpoint(TokenController tokenController, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String tokenInfoEndpointName = info.getTokenInfoEndpointName();
        this.logger.debug("Using the: {} endpoint for token introspection endpoint with multipart/form-data and application/x-www-form-urlencoded content types", tokenInfoEndpointName);
        RequestMappingInfo info = RequestMappingInfo.paths(tokenInfoEndpointName)
                .consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .methods(RequestMethod.POST)
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("tokenInfoRfc7662", String.class));
    }

    private void registryTokenEndpointJson(TokenController tokenController, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String tokenEndpointName = info.getTokenEndpointName();
        this.logger.debug("Using the: {} endpoint for token obtain endpoint with application/json content type", tokenEndpointName);
        RequestMappingInfo info = RequestMappingInfo.paths(tokenEndpointName)
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("resolveAccessTokenUsingJson", Map.class));
    }

    private void registryTokenEndpointFormData(TokenController tokenController, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String tokenEndpointName = info.getTokenEndpointName();
        this.logger.debug("Using the: {} endpoint for token obtain endpoint with multipart/form-data and application/x-www-form-urlencoded content types", tokenEndpointName);
        RequestMappingInfo info = RequestMappingInfo.paths(tokenEndpointName)
                .consumes(MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .methods(RequestMethod.POST)
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("resolveAccessTokenUsingParams", AuthorizationGrantType.class, String.class, String[].class, Map.class));
    }

    private void registryLoginEndpointFormData(KyrieOauth2Controller kyrieOauth2Controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String loginEndpointName = info.getLoginEndpointName();
        this.logger.debug("Using the: {} endpoint for login endpoint multipart/form-data and application/x-www-form-urlencoded content types", loginEndpointName);
        RequestMappingInfo info = RequestMappingInfo.paths(loginEndpointName)
                .consumes(MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .methods(RequestMethod.POST)
                .build();

        mapping.registerMapping(info, kyrieOauth2Controller, KyrieOauth2Controller.class.getDeclaredMethod("loginCheckAndGrantTypeProcessingUsingFormData", LoginDTO.class, Map.class, SessionStatus.class));
    }

    private void registryAuthorizeEndpoint(KyrieOauth2Controller kyrieOauth2Controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String authorizeEndpointName = info.getAuthorizeEndpointName();
        this.logger.debug("Using the: {} endpoint for authorize endpoint with application/json content type", authorizeEndpointName);
        RequestMappingInfo authorizeEndpointInfo =
                RequestMappingInfo.paths(authorizeEndpointName)
                        .methods(RequestMethod.GET)
                        .produces(MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
        mapping.registerMapping(authorizeEndpointInfo, kyrieOauth2Controller,
                KyrieOauth2Controller.class.getDeclaredMethod("authorization", AuthorizationRequest.class, String.class, Map.class));
    }

    private void registryLoginEndpointJson(KyrieOauth2Controller kyrieOauth2Controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String loginEndpointName = info.getLoginEndpointName();
        this.logger.debug("Using the: {} endpoint for login endpoint with application/json content type", loginEndpointName);
        RequestMappingInfo loginEndpointInfoJson = RequestMappingInfo.paths(loginEndpointName)
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
        mapping.registerMapping(loginEndpointInfoJson, kyrieOauth2Controller,
                KyrieOauth2Controller.class.getDeclaredMethod("loginCheckAndGrantTypeProcessingUsingJson", LoginDTO.class, Map.class, SessionStatus.class));
    }

    private void registryLoginUserFromSessionAndDoGrantTypeProcessing(KyrieOauth2Controller kyrieOauth2Controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String loginEndpointName = info.getLoginEndpointName();
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(loginEndpointName)
                .methods(RequestMethod.GET)
                .build();
        mapping.registerMapping(requestMappingInfo, kyrieOauth2Controller, KyrieOauth2Controller.class.getDeclaredMethod("loginUserFromRememberMeAndDoGrantTypeProcessing", HttpServletRequest.class, String.class, Map.class, SessionStatus.class));
    }

    private void registryGetConsentPage(KyrieOauth2Controller controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String consentPageEndpointName = info.getConsentPageEndpointName();
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(consentPageEndpointName)
                .methods(RequestMethod.GET)
                .build();
        mapping.registerMapping(requestMappingInfo, controller, KyrieOauth2Controller.class.getDeclaredMethod("consentPage", Map.class, HttpServletRequest.class));
    }
    private void registryHandleConsentPage(KyrieOauth2Controller controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String consentPageEndpointName = info.getConsentPageEndpointName();
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(consentPageEndpointName)
                .methods(RequestMethod.POST)
                .build();
        mapping.registerMapping(requestMappingInfo, controller, KyrieOauth2Controller.class.getDeclaredMethod("handleConsentSubmit", HttpServletRequest.class, HttpServletResponse.class, Map.class));
    }
}
