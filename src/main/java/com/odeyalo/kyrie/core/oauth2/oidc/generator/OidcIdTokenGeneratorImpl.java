package com.odeyalo.kyrie.core.oauth2.oidc.generator;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcIdToken;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenMetadata;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generator to generate an ID token from OpenID Connect
 */
@Component
public class OidcIdTokenGeneratorImpl implements OidcIdTokenGenerator {
    private final JwtTokenProvider jwtTokenProvider;
    private final String ISSUER = "http://localhost:9000";

    @Autowired
    public OidcIdTokenGeneratorImpl(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OidcIdToken generateOidcToken(String clientId, Oauth2User user) {
        return generateOidcToken(clientId, user, Collections.emptyMap());
    }

    @Override
    public OidcIdToken generateOidcToken(String clientId, Oauth2User user, Map<String, Object> additionalClaims) {

        Assert.notNull(clientId, "Client id cannot be null");
        Assert.notNull(user, "Oauth2User cannot be null");
        Assert.notNull(additionalClaims, "Claims cannot be null");

        LinkedHashMap<String, Object> claims = new LinkedHashMap<>(additionalClaims);
        claims.putIfAbsent(Claims.AUDIENCE, clientId);
        claims.putIfAbsent(Claims.ISSUER, ISSUER);
        claims.putIfAbsent(AUTH_TIME, getAuthTime());
        claims.putIfAbsent(Claims.SUBJECT, user.getId());

        TokenMetadata metadata = jwtTokenProvider.generateJwtToken(user, claims);
        return OidcIdToken
                .builder()
                .tokenValue(metadata.getToken())
                .issuedAt(Instant.ofEpochSecond(metadata.getIssuedAt()))
                .expiresIn(Instant.ofEpochSecond(metadata.getExpiresIn()))
                .claims(claims)
                .build();
    }

    protected Long getAuthTime() {
        return System.currentTimeMillis() / 1000L;
    }
}
