package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.KyrieOauth2Configurer;
import com.odeyalo.kyrie.config.KyrieOauth2ConfigurerComposite;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessor;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorContainer;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Spring configuration that create {@link Oauth2TokenCustomizerProcessorRegistry} and {@link Oauth2TokenCustomizerProcessor} beans.
 * <p>This configuration automatically configure the Oauth2TokenCustomizerProcessorRegistry using {@link KyrieOauth2Configurer}</p>
 *
 * @see KyrieOauth2Configurer
 * @see Oauth2TokenCustomizerProcessorRegistry
 */
public class CustomizersConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    @Autowired(required = false)
    public Oauth2TokenCustomizerProcessorRegistry oauth2TokenCustomizerProcessorRegistry(Set<Oauth2TokenCustomizerProcessor> customizers) {
        return new Oauth2TokenCustomizerProcessorContainer(customizers);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Collection<KyrieOauth2Configurer> configurers = applicationContext.getBeansOfType(KyrieOauth2Configurer.class).values();
        Oauth2TokenCustomizerProcessorRegistry registry = applicationContext.getBean(Oauth2TokenCustomizerProcessorRegistry.class);
        KyrieOauth2ConfigurerComposite composite = new KyrieOauth2ConfigurerComposite();
        composite.addAll(new ArrayList<>(configurers));
        composite.configureOauth2TokenCustomizers(registry);
    }
}
