package com.odeyalo.kyrie.core.oauth2.support.decorator;

import com.odeyalo.kyrie.core.oauth2.support.ImplicitFlowRedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;

/**
 * Simple {@link ImplicitFlowRedirectUrlCreationService} that just delegates all job to {@link AbstractCustomizableRedirectUriCreationServiceDecorator}
 */
public class CustomizableImplicitFlowRedirectUrlCreationServiceDecorator extends AbstractCustomizableRedirectUriCreationServiceDecorator implements ImplicitFlowRedirectUrlCreationService {

    /**
     * Create new CustomizableImplicitFlowRedirectUrlCreationServiceDecorator with delegate and customizers
     *
     * @param delegate    - parent {@link RedirectUrlCreationService} to delegate and create redirect uri that will be used to customize
     * @param customizers - customizers holder
     */
    public CustomizableImplicitFlowRedirectUrlCreationServiceDecorator(ImplicitFlowRedirectUrlCreationService delegate, Oauth2TokenCustomizerProcessorRegistry customizers) {
        super(delegate, customizers);
    }
}
