package com.odeyalo.kyrie.core.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Represent combined oauth2 token that uses to return access token with other data.
 * It useful when using multiple response types and response needs to return an access token with ID token.
 * In this case tokenValue from AbstractOauth2Token is access token and map store additional info, such other tokens and etc
 * @see AbstractOauth2Token
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
public class CombinedOauth2Token extends AbstractOauth2Token {
    /**
     * Map to store the additional info about oauth2 token, it can be token type, token value, Id token and etc.
     * Any additional info about token or additional tokens that needs to be returned to client
     */
    @Singular("addInfo")
    private Map<String, Object> additionalInfo;
}
