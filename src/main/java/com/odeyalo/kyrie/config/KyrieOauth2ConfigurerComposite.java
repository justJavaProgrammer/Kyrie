package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerViewRegistry;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2ConfigurerComposite.class);

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
        logger.info("Added all configurers: {}", configurers);
    }

    @Override
    public void configureEndpoints(Oauth2ServerEndpointsConfigurer configurer) {
        for (KyrieOauth2Configurer delegate : delegates) {
            this.logger.trace("Configure endpoints using: {}", delegate);
            delegate.configureEndpoints(configurer);
        }
    }

    @Override
    public void configureTemplates(Oauth2ServerViewRegistry viewRegistry) {
        for (KyrieOauth2Configurer delegate : delegates) {
            this.logger.trace("Configure view registry using: {}", delegate);
            delegate.configureTemplates(viewRegistry);
        }
    }

    @Override
    public void configureOauth2TokenCustomizers(Oauth2TokenCustomizerProcessorRegistry customizerProcessorRegistry) {
        for (KyrieOauth2Configurer delegate : delegates) {
            this.logger.trace("Configure Oauth2TokenCustomizerProcessorRegistry using: {}", delegate);
            delegate.configureOauth2TokenCustomizers(customizerProcessorRegistry);
        }
    }
}
