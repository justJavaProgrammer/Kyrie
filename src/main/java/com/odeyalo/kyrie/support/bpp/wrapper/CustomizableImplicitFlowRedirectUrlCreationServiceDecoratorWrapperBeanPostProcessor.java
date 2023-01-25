package com.odeyalo.kyrie.support.bpp.wrapper;

import com.odeyalo.kyrie.core.oauth2.support.ImplicitFlowRedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.support.decorator.CustomizableImplicitFlowRedirectUrlCreationServiceDecorator;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

/**
 * Simple {@link org.springframework.beans.factory.config.BeanPostProcessor}
 * that wraps all {@link ImplicitFlowRedirectUrlCreationService} implementations in {@link CustomizableImplicitFlowRedirectUrlCreationServiceDecorator}
 *
 * @see AbstractDecoratorWrapperBeanPostProcessorSupport#wrap(Object, Class)
 */
@Component
public class CustomizableImplicitFlowRedirectUrlCreationServiceDecoratorWrapperBeanPostProcessor extends AbstractDecoratorWrapperBeanPostProcessorSupport {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof ImplicitFlowRedirectUrlCreationService)) {
            return bean;
        }
        return wrap(bean, CustomizableImplicitFlowRedirectUrlCreationServiceDecorator.class);
    }
}
