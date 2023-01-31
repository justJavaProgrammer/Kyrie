package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.support.consent.ConsentPageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to return the consent page to user using redirect to endpoint provided in {@link Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo}
 */
@Component
public class ConsentPageConfigurableRedirectableAuthenticationGrantHandlerFacade extends AbstractRedirectableAuthenticationGrantHandlerFacade {
    private final Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo endpointsInfo;
    @Autowired
    private ConsentPageHandler consentPageHandler;

    public ConsentPageConfigurableRedirectableAuthenticationGrantHandlerFacade(Oauth2UserAuthenticationService oauth2UserAuthenticationService, Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo endpointsInfo) {
        super(oauth2UserAuthenticationService);
        this.endpointsInfo = endpointsInfo;
    }

    @Override
    protected HandleResult doHandleGrant(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {

        ModelAndView consentPage = consentPageHandler.getConsentPage(user, authorizationRequest, request);

        request.getSession().setAttribute("user", user);

        String redirectUri = UriComponentsBuilder.fromPath(endpointsInfo.getConsentPageEndpointName()).build().toUriString();

        return HandleResult.success(false, redirectUri);
    }
}
