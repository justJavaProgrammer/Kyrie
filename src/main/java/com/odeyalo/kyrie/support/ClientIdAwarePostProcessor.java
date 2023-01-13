package com.odeyalo.kyrie.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p>{@link BeanPostProcessor} implementation that uses to handle {@link ClientIdAware} implementations.</p>
 * <p>
 * The ClientIdAwarePostProcessor set proxy instead real {@link ClientId} to always get current ClientId instance
 * </p>
 *
 * @see org.springframework.beans.factory.Aware
 * @see ClientIdAwareValueSetterMethodInterceptor
 */
@Component
public class ClientIdAwarePostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private final Logger logger = LoggerFactory.getLogger(ClientIdAwarePostProcessor.class);
    private BeanFactory beanFactory;


    private final ClientIdAwareValueSetterMethodInterceptor methodInterceptor = new ClientIdAwareValueSetterMethodInterceptor();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof ClientIdAware)) {
            return bean;
        }

        this.logger.trace("Found ClientIdAware implementation. Creating proxy...");
        ClientIdAware clientIdAware = (ClientIdAware) bean;
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(ClientId.class);

        enhancer.setCallback(methodInterceptor);
        ClientId proxy = (ClientId) enhancer.create();

        clientIdAware.setClientId(proxy);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * <p> {@link MethodInterceptor} implementation that used to set {@link ClientId} value in {@link ClientIdAware#setClientId(ClientId)} method from BeanFactory</p>
     * <p>
     * Since ClientId class is bean that creates every request,
     * ClientIdAwareValueSetterMethodInterceptor creates proxy to access current ClientId through {@link BeanFactory}.
     * <p>Note: The {@link ClientId#getClientIdValue()} will always return current value from BeanFactory
     * and does not aware about value that was set through  {@link ClientId#setClientIdValue(String)}
     * </p>
     *
     * @version 1.0
     */
    private class ClientIdAwareValueSetterMethodInterceptor implements MethodInterceptor {
        private final Logger logger = LoggerFactory.getLogger(ClientIdAwareValueSetterMethodInterceptor.class);

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.equals(ClientId.class.getMethod("getClientIdValue"))) {
                try {
                    ClientId clientId = beanFactory.getBean(ClientId.class);
                    logger.trace("Client id value received from factory: {}", clientId);
                    return clientId.getClientIdValue();
                } catch (Exception ex) {
                    return null;
                }
            }
            return proxy.invokeSuper(obj, args);
        }
    }
}
