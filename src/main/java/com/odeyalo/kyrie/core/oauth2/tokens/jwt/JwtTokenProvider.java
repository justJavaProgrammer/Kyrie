package com.odeyalo.kyrie.core.oauth2.tokens.jwt;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenMetadata;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenValidationResult;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * Provide functionality to generate, parse, validate jwt token
 */
public interface JwtTokenProvider {

    /**
     * Generate the access token to specific user with specific claims
     * @param user   - user that granted access
     * @param claims - required claims
     * @return - AccessTokenMetadata
     */
    TokenMetadata generateJwtToken(Oauth2User user, Map<String, Object> claims);

    /**
     * Parse the jwt token and return the metadata about token, otherwise null or empty object
     *
     * @param token - token to parse
     * @return - AccessTokenMetadata or null
     */
    TokenMetadata parseToken(String token);

    /**
     * Validate the token and return validation result
     * @param token - token to validate
     * @return - TokenValidationResult with message and result status
     */
    TokenValidationResult isTokenValid(String token);

    /**
     * Parse token and return the expired time of the token in java.util.Date format
     * @param token - token to check
     * @see java.util.Date
     * @return - Token expire time in java.util.Date format
     */
    Date getExpiredJwtTokenTimeInDate(String token);

    /**
     * Same as getExpiredJwtTokenTimeInDate(String) but return java.time.LocalDateTime format
     * @param token - token to check
     * @see java.time.LocalDate
     * @return - token expire time in java.time.LocalDateTime format
     */
    default LocalDateTime getExpiredJwtTokenTime(String token) {
        Date expireTime = getExpiredJwtTokenTimeInDate(token);
        Instant instant = Instant.ofEpochMilli(expireTime.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Return claims of the token in Map<String, Object>
     * @param token - token to parse
     * @return - claims of the token in Map
     */
    Map<String, Object> getClaims(String token);
}
