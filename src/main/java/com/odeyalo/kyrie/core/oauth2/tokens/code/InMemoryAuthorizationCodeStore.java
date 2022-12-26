package com.odeyalo.kyrie.core.oauth2.tokens.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Store authorization codes in memory using concurrent map
 * @version 1.0
 */
@Component
public class InMemoryAuthorizationCodeStore implements AuthorizationCodeStore {
    private final Map<String, AuthorizationCode> store = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(InMemoryAuthorizationCodeStore.class);

    @Override
    public void save(String id, AuthorizationCode code) {
        store.put(id, code);
        this.logger.info("Saved the authorization code: {} with id: {}", code, id);
    }

    @Override
    public AuthorizationCode findById(String id) {
        return store.get(id);
    }

    @Override
    public AuthorizationCode findByAuthorizationCodeValue(String authCode) {
        if (store.size() == 0) {
            this.logger.info("The store is empty. Returning null for given authorization '{}' code", authCode);
            return null;
        }
        List<AuthorizationCode> result = store.values().stream().filter(authorizationCode -> authorizationCode.getCodeValue().equals(authCode)).collect(Collectors.toList());
        return result.size() == 0 ? null : result.get(0);
    }

    @Override
    public void delete(String id) {
        store.remove(id);
        this.logger.info("Deleted element from store with id: {}", id);
    }

    @Override
    public void delete(AuthorizationCode code) {
        store.values().removeIf(x -> x.equals(code));
    }

    @Override
    public Long deleteALl() {
        int size = store.size();
        store.clear();
        this.logger.info("Deleted all elements from store. Number elements that was deleted: {}", size);
        return (long) size;
    }

    @Override
    public Long count() {
        return (long) store.size();
    }
}
