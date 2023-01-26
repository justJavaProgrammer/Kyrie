package com.odeyalo.kyrie.core.oauth2.support.decorator;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.support.MultipleResponseTypeFlowRedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;

import java.util.List;

/**
 * Simple {@link MultipleResponseTypeFlowRedirectUrlCreationService} implementation that delegates all job to {@link AbstractCustomizableRedirectUriCreationServiceDecorator}
 */
public class CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator extends AbstractCustomizableRedirectUriCreationServiceDecorator implements MultipleResponseTypeFlowRedirectUrlCreationService {
    /**
     * Create new CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator with delegate and customizers
     *
     * @param delegate    - parent {@link CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator} to delegate and create redirect uri that will be used to customize
     * @param customizers - customizers holder
     */
    public CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator(MultipleResponseTypeFlowRedirectUrlCreationService delegate, Oauth2TokenCustomizerProcessorRegistry customizers) {
        super(delegate, customizers);
    }

    /**
     * Overridden to check for if CODE response type is presented, if so, then token will not be customized,
     * since refresh_token and other values must be obtained through only /token endpoint
     * @param request - AuthorizationRequest with all fields set
     * @param token - generated token from Oauth2FlowHandler
     * @return - original url, if response types does not contain 'code' or url that was customized otherwise
     */
    @Override
    public String createRedirectUrl(AuthorizationRequest request, Oauth2Token token) {
        if (List.of(request.getResponseTypes()).contains(Oauth2ResponseType.CODE)) {
            return delegate.createRedirectUrl(request, token);
        }
        return super.createRedirectUrl(request, token);
    }
}
