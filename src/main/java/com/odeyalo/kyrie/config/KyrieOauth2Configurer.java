package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;

/**
 * The interface provides functionality to configure a different parts of Kyrie Server.
 */
public interface KyrieOauth2Configurer  {

    /**
     * Method that can be used to change default endpoints names using {@link Oauth2ServerEndpointsConfigurer}
     * @param configurer - configurer that used to override default endpoint names
     * @see Oauth2ServerEndpointsConfigurer
     */
    default void configureEndpoints(Oauth2ServerEndpointsConfigurer configurer) {

    }
}
