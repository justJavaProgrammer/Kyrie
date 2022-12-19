package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory that uses already cached objects and never create a new one. Return null if RedirectUrlCreationService implementation was not found
 */
@Component
public class DefaultRedirectUrlCreationServiceFactory implements RedirectUrlCreationServiceFactory {
    private final Map<AuthorizationGrantType, RedirectUrlCreationService> cache;
    private final Logger logger = LoggerFactory.getLogger(DefaultRedirectUrlCreationServiceFactory.class);

    public DefaultRedirectUrlCreationServiceFactory(List<RedirectUrlCreationService>redirectUrlCreationServices) {
        this.cache = redirectUrlCreationServices.stream().collect(Collectors.toMap(RedirectUrlCreationService::supportedGrantType, Function.identity()));
        this.logger.info("Initialize factory cache with: {}", cache);
    }

    @Override
    public RedirectUrlCreationService getRedirectUrlCreationService(AuthorizationRequest request) {
        return cache.get(request.getGrantType());
    }
}
