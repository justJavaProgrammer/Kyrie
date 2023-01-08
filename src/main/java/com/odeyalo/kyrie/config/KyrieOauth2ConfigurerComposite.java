package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerViewRegistry;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple {@link KyrieOauth2Configurer} implementation   that just delegate configuration to other KyrieOauth2Configurer(s)
 *
 * @version 1.0
 * @see KyrieOauth2Configurer
 */
public class KyrieOauth2ConfigurerComposite implements KyrieOauth2Configurer {
    private final List<KyrieOauth2Configurer> delegates = new ArrayList<>();

    /**
     * Registry all configurers if list is not null or empty
     *
     * @param configurers - configurers that will be used as delegates
     */
    public void addAll(List<KyrieOauth2Configurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        delegates.addAll(configurers);
    }

    @Override
    public void configureEndpoints(Oauth2ServerEndpointsConfigurer configurer) {
        for (KyrieOauth2Configurer delegate : delegates) {
            delegate.configureEndpoints(configurer);
        }
    }

    @Override
    public void configureTemplates(Oauth2ServerViewRegistry viewRegistry) {
        for (KyrieOauth2Configurer delegate : delegates) {
            delegate.configureTemplates(viewRegistry);
        }
    }
}
