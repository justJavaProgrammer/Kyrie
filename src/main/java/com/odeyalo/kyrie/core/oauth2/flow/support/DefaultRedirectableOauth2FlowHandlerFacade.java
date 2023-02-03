package com.odeyalo.kyrie.core.oauth2.flow.support;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandler;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandlerFactory;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationServiceFactory;
import com.odeyalo.kyrie.exceptions.InvalidGrantOauth2Exception;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;
import org.springframework.stereotype.Component;

/**
 * Default {@link RedirectableOauth2FlowHandlerFacade} implementation
 */
@Component
public class DefaultRedirectableOauth2FlowHandlerFacade implements RedirectableOauth2FlowHandlerFacade {
    private final Oauth2FlowHandlerFactory oauth2FlowHandlerFactory;
    private final RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory;

    public DefaultRedirectableOauth2FlowHandlerFacade(Oauth2FlowHandlerFactory oauth2FlowHandlerFactory, RedirectUrlCreationServiceFactory redirectUrlCreationServiceFactory) {
        this.oauth2FlowHandlerFactory = oauth2FlowHandlerFactory;
        this.redirectUrlCreationServiceFactory = redirectUrlCreationServiceFactory;
    }

    @Override
    public String handleFlow(Oauth2User user, AuthorizationRequest request) throws Oauth2Exception {
        Oauth2FlowHandler flowHandler = oauth2FlowHandlerFactory.getOauth2FlowHandler(request);
        if (flowHandler == null) {
            throw new InvalidGrantOauth2Exception("The grant type is null and request cannot be performed properly!", "The grant type is null and request cannot be performed properly!");
        }
        Oauth2Token oauth2Token = flowHandler.handleFlow(request, user);

        return redirectUrlCreationServiceFactory.getRedirectUrlCreationService(request).createRedirectUrl(request, oauth2Token);
    }
}
