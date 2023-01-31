package com.odeyalo.kyrie.core.oauth2.support.decorator;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract {@link RedirectUrlCreationService} implementation that provides functionality to customize the redirect uri that was returned from parent {@link RedirectUrlCreationService}
 *
 * @see Oauth2TokenCustomizerProcessorRegistry
 */
public abstract class AbstractCustomizableRedirectUriCreationServiceDecorator implements RedirectUrlCreationService {
    protected final RedirectUrlCreationService delegate;
    protected final Oauth2TokenCustomizerProcessorRegistry customizers;
    protected final Logger logger = LoggerFactory.getLogger(AbstractCustomizableRedirectUriCreationServiceDecorator.class);

    /**
     * Create new AbstractCustomizableRedirectUriCreationServiceDecorator with delegate and customizers
     *
     * @param delegate    - parent {@link RedirectUrlCreationService} to delegate and create redirect uri that will be used to customize
     * @param customizers - customizers holder
     */
    protected AbstractCustomizableRedirectUriCreationServiceDecorator(RedirectUrlCreationService delegate, Oauth2TokenCustomizerProcessorRegistry customizers) {
        this.delegate = delegate;
        this.customizers = customizers;
    }

    @Override
    public String createRedirectUrl(AuthorizationRequest request, Oauth2Token token) {
        String rawRedirectUri = delegate.createRedirectUrl(request, token);

        this.logger.debug("Raw redirect uri from delegate: {}", rawRedirectUri);

        // Create a new UriComponentsBuilder to customize the redirect uri
        UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromUriString(rawRedirectUri);

        CombinedOauth2Token.CombinedOauth2TokenBuilder<?, ?> builder = CombinedOauth2Token.builder();

        customizers.getCustomizers().forEach(customizer -> {
            //todo
            try {
                customizer.customizeOauth2Token(token, builder);
            } catch (Exception ex) {
                this.logger.error("Oauth2TokenCustomizerProcessor threw exception, ignore the result");
            }
        });


        CombinedOauth2Token combinedOauth2Token = builder.build();

        Map<String, Object> additionalInfo = combinedOauth2Token.getAdditionalInfo();

        // Map the additional info values to String, to add it to response parameters
        Map<String, String> params = additionalInfo.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof String)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (String) entry.getValue()));

        params.forEach((key, value) -> componentsBuilder.queryParamIfPresent(key, Optional.ofNullable(value)));

        return componentsBuilder.toUriString();
    }
}
