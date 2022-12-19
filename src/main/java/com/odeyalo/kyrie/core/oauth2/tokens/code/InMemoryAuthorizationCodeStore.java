package com.odeyalo.kyrie.core.oauth2.tokens.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Store authorization codes in memory
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
        return store.values().stream().filter(authorizationCode -> authorizationCode.getCodeValue().equals(authCode)).collect(Collectors.toList()).get(0);
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }

    @Override
    public void delete(AuthorizationCode code) {
        store.values().removeIf(x -> x.equals(code));
    }
}
