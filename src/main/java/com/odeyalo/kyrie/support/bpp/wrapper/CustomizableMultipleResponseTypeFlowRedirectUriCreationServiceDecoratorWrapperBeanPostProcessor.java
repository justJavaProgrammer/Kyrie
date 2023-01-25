package com.odeyalo.kyrie.support.bpp.wrapper;

import com.odeyalo.kyrie.core.oauth2.support.MultipleResponseTypeFlowRedirectUrlCreationService;
import com.odeyalo.kyrie.core.oauth2.support.decorator.CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

/**
 * Simple {@link org.springframework.beans.factory.config.BeanPostProcessor}
 * used to wrap all {@link MultipleResponseTypeFlowRedirectUrlCreationService} in {@link CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator}
 *
 * @see AbstractDecoratorWrapperBeanPostProcessorSupport#wrap(Object, Class)
 */
@Component
public class CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecoratorWrapperBeanPostProcessor extends AbstractDecoratorWrapperBeanPostProcessorSupport {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof MultipleResponseTypeFlowRedirectUrlCreationService)) {
            return bean;
        }
        return wrap(bean, CustomizableMultipleResponseTypeFlowRedirectUriCreationServiceDecorator.class);
    }
}
