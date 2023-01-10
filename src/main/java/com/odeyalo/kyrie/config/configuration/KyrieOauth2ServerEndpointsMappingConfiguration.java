package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.KyrieOauth2Configurer;
import com.odeyalo.kyrie.config.KyrieOauth2ConfigurerComposite;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerViewRegistry;
import com.odeyalo.kyrie.controllers.KyrieOauth2Controller;
import com.odeyalo.kyrie.controllers.TokenController;
import com.odeyalo.kyrie.controllers.support.AuthorizationRequestValidator;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.Oauth2TokenGeneratorFacade;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationServiceFactory;
import com.odeyalo.kyrie.core.oauth2.support.grant.AuthorizationGrantTypeResolver;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessTokenManager;
import com.odeyalo.kyrie.dto.GetAccessTokenRequestDTO;
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
    private final KyrieOauth2ConfigurerComposite configurer = new KyrieOauth2ConfigurerComposite();
    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2ServerEndpointsMappingConfiguration.class);

    private Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo info;
    private final ViewResolver viewResolver;

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
        Oauth2ServerEndpointsConfigurer configurer = new Oauth2ServerEndpointsConfigurer();
        this.configurer.configureEndpoints(configurer);
        this.info = configurer.buildOauth2ServerEndpointsInfo();
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

//        registryTokenEndpointFormData(tokenController, mapping);

        registryTokenInfoEndpoint(tokenController, mapping);

        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return mapping;
            }
        };
    }

    @Bean
    public KyrieOauth2Controller kyrieOauth2Controller(Oauth2UserAuthenticationService authenticationService,
                                                       Oauth2FlowHandlerFactory factory,
                                                       AuthorizationGrantTypeResolver resolver,
                                                       RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory,
                                                       AuthorizationRequestValidator validator,
                                                       TemplateResolver templateResolver) {
        return new KyrieOauth2Controller(authenticationService, factory, resolver, redirectUrlCreationServiceFactory, validator, templateResolver);
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
    public TemplateResolver templateResolver(List<ModelEnhancerPostProcessor> processors) throws Exception {
        Oauth2ServerViewRegistry viewRegistry = new Oauth2ServerViewRegistry();

        configurer.configureTemplates(viewRegistry);

        Map<String, View> views = viewRegistry.getViews();
        View loginView = viewResolver.resolveViewName(DEFAULT_LOGIN_TEMPLATE_NAME, Locale.ENGLISH);

        views.putIfAbsent(DefaultTemplateResolver.LOGIN_TEMPLATE_TYPE, loginView);

        DefaultTemplateResolver defaultTemplateResolver = new DefaultTemplateResolver(processors);

        for (Map.Entry<String, View> entry : views.entrySet()) {
            String templateType = entry.getKey();
            View view = entry.getValue();
            defaultTemplateResolver.addTemplate(templateType, view);
        }
        return defaultTemplateResolver;
    }

    @Bean
    public TokenController tokenController(Oauth2AccessTokenManager accessTokenManager, Oauth2TokenGeneratorFacade tokenGeneratorFacade) {
        return new TokenController(accessTokenManager, tokenGeneratorFacade);
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
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("resolveAccessTokenUsingJson", AuthorizationGrantType.class, String.class, String[].class, Map.class));
    }

    private void registryTokenEndpointFormData(TokenController tokenController, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        String tokenEndpointName = info.getTokenEndpointName();
        this.logger.debug("Using the: {} endpoint for token obtain endpoint with multipart/form-data and application/x-www-form-urlencoded content types", tokenEndpointName);
        RequestMappingInfo info = RequestMappingInfo.paths(tokenEndpointName)
                .consumes(MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .methods(RequestMethod.POST)
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("resolveAccessTokenUsingFormData", GetAccessTokenRequestDTO.class));
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
                KyrieOauth2Controller.class.getDeclaredMethod("authorization", String.class, Oauth2ResponseType[].class, String[].class, String.class, String.class, Map.class));
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
}
