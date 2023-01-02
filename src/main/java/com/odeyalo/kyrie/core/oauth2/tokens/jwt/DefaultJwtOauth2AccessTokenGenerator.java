package com.odeyalo.kyrie.core.oauth2.tokens.jwt;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenMetadata;

import java.time.Instant;
import java.util.Collections;

/**
 * Generate an access token with specific scopes
 */
public class DefaultJwtOauth2AccessTokenGenerator implements Oauth2AccessTokenGenerator {
    private final JwtTokenProvider jwtTokenProvider;

    public DefaultJwtOauth2AccessTokenGenerator(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Oauth2AccessToken generateAccessToken(Oauth2User user, String[] scopes) {
        TokenMetadata metadata = jwtTokenProvider.generateJwtToken(user, Collections.singletonMap(SCOPE, String.join(" ", scopes)));
        return Oauth2AccessToken.builder()
                .tokenType(Oauth2AccessToken.TokenType.BEARER)
                .tokenValue(metadata.getToken())
                .issuedAt(Instant.ofEpochSecond(metadata.getIssuedAt()))
                .expiresIn(Instant.ofEpochSecond(metadata.getExpiresIn()))
                .scope(String.join(" ", scopes))
                .build();
    }
}
