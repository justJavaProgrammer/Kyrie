package com.odeyalo.kyrie.core.oauth2.tokens.customizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Default {@link Oauth2TokenCustomizerProcessorRegistry} implementation.
 *
 * @see Oauth2TokenCustomizerProcessorRegistry
 */
public class Oauth2TokenCustomizerProcessorContainer implements Oauth2TokenCustomizerProcessorRegistry {
    private final Set<Oauth2TokenCustomizerProcessor> customizers;
    private final Logger logger = LoggerFactory.getLogger(Oauth2TokenCustomizerProcessorContainer.class);

    public Oauth2TokenCustomizerProcessorContainer() {
        this.customizers = new HashSet<>();
        logger.debug("Created Oauth2TokenCustomizerProcessorContainer with empty customizers");
    }

    public Oauth2TokenCustomizerProcessorContainer(Set<Oauth2TokenCustomizerProcessor> customizers) {
        Assert.notNull(customizers, "Customizers must be not null");
        this.customizers = customizers;
        if (customizers.isEmpty()) {
            logger.debug("Created Oauth2TokenCustomizerProcessorContainer with empty customizers");
            return;
        }
        logger.debug("Created Oauth2TokenCustomizerProcessorContainer with {} number of customizers", customizers.size());
        logger.debug("Will customize Oauth2Token with: {}", customizers);
    }

    @Override
    public void registryCustomizer(Oauth2TokenCustomizerProcessor customizer) {
        customizers.add(customizer);
        logger.debug("Added customizer: {}", customizer);
    }

    @Override
    public void removeCustomizer(Oauth2TokenCustomizerProcessor customizer) {
        boolean hasRemoved = customizers.remove(customizer);
        if (hasRemoved) {
            logger.debug("Removed customizer: {}", customizer);
        }
    }

    @Override
    public Set<Oauth2TokenCustomizerProcessor> getCustomizers() {
        return customizers;
    }
}
