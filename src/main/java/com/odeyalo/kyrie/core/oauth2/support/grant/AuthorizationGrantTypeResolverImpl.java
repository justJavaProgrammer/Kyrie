package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class AuthorizationGrantTypeResolverImpl implements AuthorizationGrantTypeResolver {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationGrantTypeResolverImpl.class);
    private final Map<Oauth2ResponseType[], AuthorizationGrantType> cache = new ConcurrentHashMap<>();

    @Override
    public AuthorizationGrantType resolveGrantType(Oauth2ResponseType... oauth2ResponseTypes) {
        AuthorizationGrantType cachedGrantType = cache.get(oauth2ResponseTypes);
        if (cachedGrantType != null) {
            return cachedGrantType;
        }
        // Sort to make AuthorizationGrantType with minimum supported response types first
        // AuthorizationGrantType's with ONE element will ALWAYS be first.
        Collection<AuthorizationGrantType> values = Arrays.stream(AuthorizationGrantType.values())
                .sorted(Comparator.comparingInt(grantType -> grantType.getSupportedResponseTypes().length))
                .collect(Collectors.toList());

        for (AuthorizationGrantType value : values) {
            Oauth2ResponseType[] supportedResponseTypes = value.getSupportedResponseTypes();
            if (List.of(supportedResponseTypes).containsAll(List.of(oauth2ResponseTypes))) {
                logger.debug("Cached: {} with key: {}", value, Arrays.toString(oauth2ResponseTypes));
                cache.put(oauth2ResponseTypes, value);
                return value;
            }
        }
        return null;
    }
}
