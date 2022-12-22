package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Default Oauth2AccessTokenManager implementation that uses jwt as access token
 */
@Service
public class DefaultJwtOauth2AccessTokenManager implements Oauth2AccessTokenManager {
    private final JwtTokenProvider jwtTokenProvider;
    private final AccessTokenReturner returner;
    private final Oauth2AccessTokenGenerator generator;

    @Autowired
    public DefaultJwtOauth2AccessTokenManager(JwtTokenProvider jwtTokenProvider, AccessTokenReturner returner, Oauth2AccessTokenGenerator generator) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.returner = returner;
        this.generator = generator;
    }

    @Override
    public Oauth2AccessToken generateAccessToken(Oauth2User user, String[] scopes) {
       return generator.generateAccessToken(user, scopes);
    }

    @Override
    public Oauth2AccessToken getTokenInfo(String token) {
        TokenValidationResult result = jwtTokenProvider.isTokenValid(token);
        if (!result.isValid()) {
            return Oauth2AccessToken.alreadyExpired();
        }
        TokenMetadata metadata = jwtTokenProvider.parseToken(token);
        Object scopeClaim = metadata.getClaims().get(Oauth2AccessTokenGenerator.SCOPE);
        String scopes = resolveScopes(scopeClaim);
        Long expiresIn = metadata.getExpiresIn();
        return Oauth2AccessToken.builder()
                .issuedAt(Instant.ofEpochSecond(metadata.getIssuedAt()))
                .expiresIn(Instant.ofEpochSecond(expiresIn))
                .tokenType(Oauth2AccessToken.TokenType.BEARER)
                .tokenValue(metadata.getToken())
                .scope(scopes)
                .build();
    }

    @Override
    public TokenValidationResult validateAccessToken(String token) {
        return jwtTokenProvider.isTokenValid(token);
    }

    @Override
    public Oauth2AccessToken obtainAccessTokenByAuthorizationCode(Oauth2ClientCredentials clientCredentials, String authorizationCode) {
        return returner.getToken(clientCredentials, authorizationCode);
    }

    private String resolveScopes(Object scopeClaim) {
        String scopes = null;
        if (scopeClaim instanceof String) {
            scopes = (String) scopeClaim;
        } else if (scopeClaim instanceof List) {
            List<String> scopesList = (List<String>) scopeClaim;
            scopes = String.join(" ", scopesList);
        }
        return scopes;
    }
}
