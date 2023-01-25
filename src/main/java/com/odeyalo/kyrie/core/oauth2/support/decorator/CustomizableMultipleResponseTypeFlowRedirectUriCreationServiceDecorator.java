package com.odeyalo.kyrie.core.oauth2.support.decorator;

import com.odeyalo.kyrie.core.oauth2.support.MultipleResponseTypeFlowRedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;

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
    protected CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator(RedirectUrlCreationService delegate, Oauth2TokenCustomizerProcessorRegistry customizers) {
        super(delegate, customizers);
    }
}
