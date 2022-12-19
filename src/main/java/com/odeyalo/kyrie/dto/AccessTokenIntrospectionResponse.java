package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represent the json response for access token introspection, based on RFC 7662 specification
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7662#section-2.2">Introspection Response</a>
 */
@Builder(builderMethodName = "hiddenBuilder")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AccessTokenIntrospectionResponse {
    /**
     * REQUIRED.
     * <p> Boolean indicator of whether or not the presented token
     * is currently active.  The specifics of a token's "active" state
     * will vary depending on the implementation of the authorization
     * server and the information it keeps about its tokens, but a "true"
     * value return for the "active" property will generally indicate
     * that a given token has been issued by this authorization server,
     * has not been revoked by the resource owner, and is within its
     * given time window of validity (e.g., after its issuance time and
     * before its expiration time).</p>
     */
    private final boolean active;
    /**
     * Represent the optional 'scope' field in response.
     * <p>OPTIONAL.</p>
     * <p>A JSON string containing a space-separated list of
     * scopes associated with this token
     * </p>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String scope;
    /**
     * Represent the optional 'exp' field in response.
     * <p>OPTIONAL.</p>
     * <p>Integer timestamp, measured in the number of seconds
     * since January 1 1970 UTC, indicating when this token will expire,
     * as defined in JWT</p>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("exp")
    private Long expiresIn;

    /**
     * Helper method to create non active access token
     * @return - token with all fields null and active field set to false
     */
    public static AccessTokenIntrospectionResponse nonActive() {
        return new AccessTokenIntrospectionResponse(false);
    }

    public static AccessTokenIntrospectionResponseBuilder builder(boolean active) {
        return new AccessTokenIntrospectionResponseBuilder().active(active);
    }
}
