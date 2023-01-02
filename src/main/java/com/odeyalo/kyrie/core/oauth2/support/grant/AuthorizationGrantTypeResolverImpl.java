package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AuthorizationGrantTypeResolverImpl implements AuthorizationGrantTypeResolver {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationGrantTypeResolverImpl.class);
    private final Map<Oauth2ResponseType[], AuthorizationGrantType> cache;

    private final List<AuthorizationGrantType> sortedGrantTypes;

    /**
     * Create AuthorizationGrantTypeResolverImpl with default values
     */
    public AuthorizationGrantTypeResolverImpl() {
        this.sortedGrantTypes = getSortedGrantTypes();
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Create AuthorizationGrantTypeResolverImpl with custom cache
     * @param cache - cache that will be used to return AuthorizationGrantType by response types
     */
    public AuthorizationGrantTypeResolverImpl(Map<Oauth2ResponseType[], AuthorizationGrantType> cache) {
        this.sortedGrantTypes = getSortedGrantTypes();
        if (cache.containsKey(null) || cache.containsValue(null)) {
            throw new IllegalStateException("Cache can't store null values!");
        }
        this.cache = cache;
    }

    /**
     * Create AuthorizationGrantTypeResolverImpl with custom cache and sorted grant types
     * @param sortedGrantTypes - sorted grant types in descending order by length of supported response types
     * @param cache - cache that will be used to return AuthorizationGrantType
     */
    public AuthorizationGrantTypeResolverImpl(List<AuthorizationGrantType> sortedGrantTypes, Map<Oauth2ResponseType[], AuthorizationGrantType> cache) {
        if (cache.containsKey(null) || cache.containsValue(null)) {
            throw new IllegalStateException("Cache can't store null values!");
        }
        if (sortedGrantTypes.contains(null)) {
            throw new IllegalStateException("Sorted grant types contains null: " + sortedGrantTypes);
        }
        this.cache = cache;
        this.sortedGrantTypes = sortedGrantTypes;
    }


    @Override
    public AuthorizationGrantType resolveGrantType(Oauth2ResponseType... oauth2ResponseTypes) {
        AuthorizationGrantType cachedGrantType = cache.get(oauth2ResponseTypes);
        if (cachedGrantType != null) {
            return cachedGrantType;
        }

        for (AuthorizationGrantType value : sortedGrantTypes) {
            Oauth2ResponseType[] supportedResponseTypes = value.getSupportedResponseTypes();
            if (List.of(supportedResponseTypes).containsAll(List.of(oauth2ResponseTypes))) {
                logger.debug("Cached: {} with key: {}", value, Arrays.toString(oauth2ResponseTypes));
                cache.put(oauth2ResponseTypes, value);
                return value;
            }
        }
        return null;
    }

    // Sort to make AuthorizationGrantType with minimum supported response types first
    // AuthorizationGrantType's with ONE element will ALWAYS be first.
    private List<AuthorizationGrantType> getSortedGrantTypes() {
        return Arrays.stream(AuthorizationGrantType.values())
                .sorted(Comparator.comparingInt(grantType -> grantType.getSupportedResponseTypes().length))
                .collect(Collectors.toList());
    }
}
