package com.odeyalo.kyrie.core.oauth2.tokens.refresh;

import com.odeyalo.kyrie.core.oauth2.RefreshToken;
import com.odeyalo.kyrie.support.ClientId;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

/**
 * {@link RefreshTokenProvider} implementation that working with only opaque tokens.
 * <p>Opaque token is fully random string that stored in database</p>
 */
@Component
public class OpaqueRefreshTokenProvider implements RefreshTokenProvider {
    private final RefreshTokenRepository refreshTokenRepository;

    public OpaqueRefreshTokenProvider(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken generateToken(ClientId clientId, String[] scopes) {
        String opaqueToken = RandomStringUtils.randomAlphanumeric(30);

        String clientIdValue = clientId.getClientIdValue();

        RefreshToken token = RefreshToken.builder()
                .tokenValue(opaqueToken)
                .active(true)
                .clientId(clientIdValue)
                .scopes(scopes)
                .build();
        // Using client id as refresh token id, to avoid unnecessary id generation
        refreshTokenRepository.save(clientIdValue, token);

        return token;
    }

    @Override
    public RefreshToken getTokenByValue(String tokenValue) {
        return refreshTokenRepository.findByTokenValue(tokenValue);
    }

    @Override
    public void deactivateToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenValue(tokenValue);
        refreshToken.setActive(false);
        refreshTokenRepository.save(refreshToken.getClientId(), refreshToken);
    }

    @Override
    public void removeToken(String tokenValue) {
        refreshTokenRepository.deleteByTokenValue(tokenValue);
    }
}
