package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.controllers.KyrieOauth2Controller;
import com.odeyalo.kyrie.controllers.TokenController;
import com.odeyalo.kyrie.controllers.support.AuthorizationRequestValidator;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.Oauth2TokenGeneratorFacade;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationServiceFactory;
import com.odeyalo.kyrie.core.oauth2.support.grant.AuthorizationGrantTypeResolver;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessTokenManager;
import com.odeyalo.kyrie.dto.GetAccessTokenRequestDTO;
import com.odeyalo.kyrie.dto.LoginDTO;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * <p>
 * Configuration that uses to add Kyrie endpoints to {@link org.springframework.web.servlet.DispatcherServlet}
 * using {@link WebMvcRegistrations}
 * </p>
 *
 * @version 1.0
 */
//@AutoConfigureOrder()
    @EnableWebMvc
public class KyrieOauth2ServerEndpointsMappingConfiguration {


    @Bean
    public WebMvcRegistrations webMvcRegistrations(KyrieOauth2Controller kyrieOauth2Controller,
                                                   TokenController tokenController,
                                                   RequestMappingHandlerMapping mapping) throws Exception{

        registryAuthorizeEndpoint(kyrieOauth2Controller, mapping);

        registryLoginEndpointJson(kyrieOauth2Controller, mapping);

        registryLoginEndpointFormData(kyrieOauth2Controller, mapping);

        registryTokenEndpointJson(tokenController, mapping);

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
    public KyrieOauth2Controller kyrieOauth2Controller(Oauth2UserAuthenticationService authenticationService,
                                                       Oauth2FlowHandlerFactory factory,
                                                       AuthorizationGrantTypeResolver resolver,
                                                       RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory,
                                                       AuthorizationRequestValidator validator) {
        return new KyrieOauth2Controller(authenticationService, factory, resolver, redirectUrlCreationServiceFactory, validator);
    }


    @Bean
    public TokenController tokenController(Oauth2AccessTokenManager accessTokenManager, Oauth2TokenGeneratorFacade tokenGeneratorFacade) {
        return new TokenController(accessTokenManager, tokenGeneratorFacade);
    }

    private void registryTokenInfoEndpoint(TokenController tokenController, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        RequestMappingInfo info = RequestMappingInfo.paths("/tokeninfo")
                .consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .methods(RequestMethod.POST)
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("tokenInfoRfc7662", String.class));
    }

    private void registryTokenEndpointJson(TokenController tokenController, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        RequestMappingInfo info = RequestMappingInfo.paths("/token")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .methods(RequestMethod.POST)
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("resolveAccessTokenUsingJson", GetAccessTokenRequestDTO.class));
    }

    private void registryTokenEndpointFormData(TokenController tokenController, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        RequestMappingInfo info = RequestMappingInfo.paths("/token")
                .consumes(MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .methods(RequestMethod.POST)
                .build();
        mapping.registerMapping(info, tokenController, TokenController.class.getDeclaredMethod("resolveAccessTokenUsingFormData", GetAccessTokenRequestDTO.class));
    }

    private void registryLoginEndpointFormData(KyrieOauth2Controller kyrieOauth2Controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        RequestMappingInfo info = RequestMappingInfo.paths("/oauth2/login")
                .consumes(MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .methods(RequestMethod.POST)
                .build();

        mapping.registerMapping(info, kyrieOauth2Controller, KyrieOauth2Controller.class.getDeclaredMethod("loginCheckAndGrantTypeProcessingUsingFormData", LoginDTO.class, Map.class, SessionStatus.class));
    }

    private void registryAuthorizeEndpoint(KyrieOauth2Controller kyrieOauth2Controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        RequestMappingInfo authorizeEndpointInfo =
                RequestMappingInfo.paths("/oauth2/authorize")
                .methods(RequestMethod.GET)
                        .produces(MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
        mapping.registerMapping(authorizeEndpointInfo, kyrieOauth2Controller,
                KyrieOauth2Controller.class.getDeclaredMethod("authorization", String.class, Oauth2ResponseType[].class, String[].class, String.class, String.class, Map.class));
    }

    private void registryLoginEndpointJson(KyrieOauth2Controller kyrieOauth2Controller, RequestMappingHandlerMapping mapping) throws NoSuchMethodException {
        RequestMappingInfo loginEndpointInfoJson = RequestMappingInfo.paths("/oauth2/login")
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
        mapping.registerMapping(loginEndpointInfoJson, kyrieOauth2Controller,
                KyrieOauth2Controller.class.getDeclaredMethod("loginCheckAndGrantTypeProcessingUsingJson", LoginDTO.class, Map.class, SessionStatus.class));
    }
}
