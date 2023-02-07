package com.odeyalo.kyrie.core.support.condition;

import com.odeyalo.kyrie.core.Oauth2User;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link RememberUserCondition} implementation that returns the result based on property from 'application.properties' file
 */
public class PropertyConditionalRememberUserCondition implements RememberUserCondition, EnvironmentAware {
    private Environment environment;
    private boolean cachedValue;
    public static final String PROPERTY_NAME = "kyrie.oauth2.auth.remember-me.enabled";

    @PostConstruct
    public void cacheProps() {
        if (environment == null) {
            this.cachedValue = false;
            return;
        }
        this.cachedValue = environment.getProperty(PROPERTY_NAME, boolean.class, false);
    }

    @Override
    public boolean shouldRememberUser(Oauth2User oauth2User, HttpServletRequest request) {
        return cachedValue;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean isCachedValue() {
        return cachedValue;
    }

    public void setCachedValue(boolean cachedValue) {
        this.cachedValue = cachedValue;
    }

    /**
     * Method used to refresh the current cached properties and update the value that returned from {@link #shouldRememberUser(Oauth2User, HttpServletRequest)}
     */
    public void refresh() {
        cacheProps();
    }
}
