package com.odeyalo.kyrie.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.authorization.support.AuthorizationRequestContextHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Custom BeanPostProcessor that used to inject {@link AuthorizationRequest} in {@link AuthorizationRequestAware} implementations.
 *
 * @see org.springframework.beans.factory.Aware
 * @see AuthorizationRequest
 * @see BeanPostProcessor
 * @see org.springframework.context.support.ApplicationContextAwareProcessor
 *
 * @version 1.0
 */
@Component
public class AuthorizationRequestAwarePostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof AuthorizationRequestAware)) {
            return bean;
        }
        AuthorizationRequestAware aware = (AuthorizationRequestAware) bean;


        AuthorizationRequestProxy proxy = new AuthorizationRequestProxy();

        aware.setAuthorizationRequest(proxy);

        return bean;
    }

    /**
     * Simple proxy class that overrides all getters and resolves current {@link AuthorizationRequest}
     * through {@link AuthorizationRequestContextHolder#getContext()}
     *
     * @see AuthorizationRequest
     * @see AuthorizationRequestContextHolder
     */
    private static class AuthorizationRequestProxy extends AuthorizationRequest {

        @Override
        public String getClientId() {
            AuthorizationRequest request = AuthorizationRequestContextHolder.getContext().getRequest();
            return request != null ? request.getClientId() : null;
        }

        @Override
        public String getRedirectUrl() {
            AuthorizationRequest request = AuthorizationRequestContextHolder.getContext().getRequest();
            return request != null ? request.getRedirectUrl() : null;
        }

        @Override
        public String[] getScopes() {
            AuthorizationRequest request = AuthorizationRequestContextHolder.getContext().getRequest();
            return request != null ? request.getScopes() : null;
        }

        @Override
        public Oauth2ResponseType[] getResponseTypes() {
            AuthorizationRequest request = AuthorizationRequestContextHolder.getContext().getRequest();
            return request != null ? request.getResponseTypes() : null;
        }

        @Override
        public AuthorizationGrantType getGrantType() {
            AuthorizationRequest request = AuthorizationRequestContextHolder.getContext().getRequest();
            return request != null ? request.getGrantType() : null;
        }

        @Override
        public String getState() {
            AuthorizationRequest request = AuthorizationRequestContextHolder.getContext().getRequest();
            return request != null ? request.getState() : null;
        }
    }
}
