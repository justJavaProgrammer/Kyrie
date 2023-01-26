package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerViewRegistry;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;

/**
 * The interface provides functionality to customize different parts of Kyrie Server.
 */
public interface KyrieOauth2Configurer  {

    /**
     * Method that can be used to change default endpoints names using {@link Oauth2ServerEndpointsConfigurer}
     * @param configurer - configurer that used to override default endpoint names
     * @see Oauth2ServerEndpointsConfigurer
     */
    default void configureEndpoints(Oauth2ServerEndpointsConfigurer configurer) {

    }

    /**
     * Method that can be used to change default views, add a new one or delete
     * @see Oauth2ServerViewRegistry
     * @param viewRegistry - registry to configure
     */
    default void configureTemplates(Oauth2ServerViewRegistry viewRegistry) {

    }

    /**
     * The method can be used to customize the {@link Oauth2TokenCustomizerProcessorRegistry}, add or remove the {@link com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessor}
     * @param customizerProcessorRegistry - registry to customize
     */
    default void configureOauth2TokenCustomizers(Oauth2TokenCustomizerProcessorRegistry customizerProcessorRegistry) {

    }
}
