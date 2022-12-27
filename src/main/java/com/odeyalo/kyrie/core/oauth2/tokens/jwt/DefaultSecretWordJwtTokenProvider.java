package com.odeyalo.kyrie.core.oauth2.tokens.jwt;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenMetadata;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenValidationResult;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Default JwtTokenProvider implementation that sign jwt token with secret word
 */
@Service
public class DefaultSecretWordJwtTokenProvider implements JwtTokenProvider {
    private static final long JWT_TOKEN_EXPIRATION_TIME_SECONDS = 3600L;
    private final Logger logger = LoggerFactory.getLogger(DefaultSecretWordJwtTokenProvider.class);
    private final String secretWord;

    @Autowired
    public DefaultSecretWordJwtTokenProvider(@Value("${kyrie.tokens.jwt.secret.key}") String secretWord) {
        this.secretWord = secretWord;
    }

    @Override
    public TokenMetadata generateJwtToken(Oauth2User user, Map<String, Object> claims) {
        // Copy claims to other Map since claims from parameters can be immutable Map
        Map<String, Object> copiedClaims = new HashMap<>(claims);
        copiedClaims.putIfAbsent(Claims.SUBJECT, user.getId());
        long issuedAt = getIssuedAt();
        copiedClaims.putIfAbsent(Claims.ISSUED_AT, issuedAt);
        Date exp = new Date(System.currentTimeMillis() + JWT_TOKEN_EXPIRATION_TIME_SECONDS * 1000L);

        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretWord)
                .setExpiration(exp)
                .addClaims(copiedClaims)
                .compact();
        return new TokenMetadata(true, token, issuedAt, exp.getTime() / 1000, copiedClaims);
    }


    @Override
    public TokenMetadata parseToken(String token) {
        TokenValidationResult result = isTokenValid(token);
        if (!result.isValid()) {
            return TokenMetadata.invalid();
        }
        return doParseToken(token);
    }

    @Override
    public Date getExpiredJwtTokenTimeInDate(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }

    @Override
    public TokenValidationResult isTokenValid(String token) {
        try {
            getParser().parseClaimsJws(token);
            return TokenValidationResult.valid();
        } catch (SignatureException e) {
            this.logger.error("Invalid JWT signature: {}", e.getMessage());
            return TokenValidationResult.invalid(String.format("Invalid JWT signature: %s", e.getMessage()));
        } catch (MalformedJwtException e) {
            this.logger.error("Invalid JWT token: {}", e.getMessage());
            return TokenValidationResult.invalid(String.format("Invalid JWT token: %s", e.getMessage()));
        } catch (ExpiredJwtException e) {
            this.logger.error("JWT token is expired: {}", e.getMessage());
            return TokenValidationResult.invalid(String.format("JWT token is expired: %s", e.getMessage()));
        } catch (UnsupportedJwtException e) {
            this.logger.error("JWT token is unsupported: {}", e.getMessage());
            return TokenValidationResult.invalid(String.format("JWT token is unsupported: %s", e.getMessage()));
        } catch (IllegalArgumentException e) {
            this.logger.error("JWT claims string is empty: {}", e.getMessage());
            return TokenValidationResult.invalid(String.format("JWT claims string is empty: %s", e.getMessage()));
        }
    }

    @Override
    public Claims getClaims(String token) {
        TokenValidationResult tokenValid = isTokenValid(token);
        if (!tokenValid.isValid()) {
            return null;
        }
        return doGetClaims(token);
    }

    protected TokenMetadata doParseToken(String token) {
        Date time = getExpiredJwtTokenTimeInDate(token);
        Claims claims = getClaims(token);
        Long issuedAt = getIssuedAt(claims);
        return new TokenMetadata(true, token, issuedAt, time.getTime() / 1000, claims);
    }

    private Long getIssuedAt(Claims claims) {
        Object obj = claims.getOrDefault(Claims.ISSUED_AT, -1);
        return ((Number) obj).longValue();
    }

    protected Claims doGetClaims(String token) {
        return getParser().parseClaimsJws(token).getBody();
    }


    protected JwtParser getParser() {
        return Jwts.parser().setSigningKey(secretWord);
    }

    protected long getIssuedAt() {
        return System.currentTimeMillis() / 1000L;
    }

}
