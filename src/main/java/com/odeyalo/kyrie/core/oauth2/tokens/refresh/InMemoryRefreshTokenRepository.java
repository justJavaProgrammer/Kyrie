package com.odeyalo.kyrie.core.oauth2.tokens.refresh;

import com.odeyalo.kyrie.core.oauth2.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Store te refresh token in Map
 */
@Component
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {
    private final Map<String, RefreshToken> store;
    private final Logger logger = LoggerFactory.getLogger(InMemoryRefreshTokenRepository.class);

    public InMemoryRefreshTokenRepository() {
        this.store = new HashMap<>();
    }

    public InMemoryRefreshTokenRepository(Map<String, RefreshToken> store) {
        this.store = store;
    }

    @Override
    public void save(String id, RefreshToken token) {
        store.put(id, token);
        logger.info("Saved token with id: {}, {}", id, token);
    }

    @Override
    public RefreshToken findById(String id) {
        return store.get(id);
    }

    @Override
    public RefreshToken findByTokenValue(String tokenValue) {
        return store.values().stream().filter(token -> token.getTokenValue().equals(tokenValue)).findFirst().orElse(null);
    }

    @Override
    public void update(String oldTokenId, RefreshToken newToken) {
        store.put(oldTokenId, newToken);
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public void deleteByTokenValue(String tokenValue) {
        store.values().removeIf(token -> token.getTokenValue().equals(tokenValue));
    }
}
