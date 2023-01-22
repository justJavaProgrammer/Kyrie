package com.odeyalo.kyrie.core.oauth2.tokens.customizer;

import java.util.Set;

/**
 * A simple registry interface that used to registry {@link Oauth2TokenCustomizerProcessor}.
 * <p>It useful for store all Oauth2TokenCustomizerProcessor in one place, it also supports lambda</p>
 *
 * @see Oauth2TokenCustomizerProcessor
 * @version 1.0
 */
public interface Oauth2TokenCustomizerProcessorRegistry {

    /**
     * Registry the {@link Oauth2TokenCustomizerProcessor} that will be invoked for ALL grant types
     * @param customizer - customizer to registry
     */
    void registryCustomizer(Oauth2TokenCustomizerProcessor customizer);

    /**
     * Remove customizer from registry, do nothing if customizer was not found
     * @param customizer - customizer to remove
     */
    void removeCustomizer(Oauth2TokenCustomizerProcessor customizer);

    /**
     * Returns all registered customizers
     * @return - Set of customizers that were previously registered
     */
    Set<Oauth2TokenCustomizerProcessor> getCustomizers();
}
