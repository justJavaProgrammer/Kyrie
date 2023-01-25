package com.odeyalo.kyrie.support.bpp.wrapper;

import com.odeyalo.kyrie.core.oauth2.support.AuthorizationCodeFlowRedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.support.decorator.EnhancedAuthorizationCodeFlowRedirectUrlCreationServiceDecorator;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation that wraps the {@link AuthorizationCodeFlowRedirectUrlCreationService} in {@link EnhancedAuthorizationCodeFlowRedirectUrlCreationServiceDecorator}
 */
@Component
public class EnhancedAuthorizationCodeFlowRedirectUrlCreationServiceDecoratorWrapperBeanPostProcessor extends AbstractDecoratorWrapperBeanPostProcessorSupport {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof AuthorizationCodeFlowRedirectUrlCreationService)) {
            return bean;
        }
        return wrap(bean, EnhancedAuthorizationCodeFlowRedirectUrlCreationServiceDecorator.class);
    }
}
