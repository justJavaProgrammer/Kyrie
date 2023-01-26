package com.odeyalo.kyrie.core.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * <p> Represent combined oauth2 token that uses to return access token with other data.</p>
 * <p> It useful when using multiple response types and response needs to return an access token with ID token.</p>
 * <p> In this case tokenValue from AbstractOauth2Token is access token and map store additional info, such other tokens and etc</p>
 *
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

    /**
     * Constructs the CombinedOauth2Token object from parent {@link Oauth2Token} object
     *
     * @param parent - parent Oauth2Token
     */
    protected CombinedOauth2Token(Oauth2Token parent) {
        this.tokenValue = parent.getTokenValue();
        this.issuedAt = parent.getIssuedAt();
        this.expiresIn = parent.getExpiresIn();
    }

    /**
     * Constructs the CombinedOauth2Token object from parent {@link Oauth2Token} object and add additional info.
     * @param parent - parent {@link Oauth2Token} object
     * @param additionalInfo - info that must be added to object
     */
    protected CombinedOauth2Token(Oauth2Token parent, Map<String, Object> additionalInfo) {
        this(parent);
        this.additionalInfo = additionalInfo;
    }

    public static CombinedOauth2Token from(Oauth2Token parent) {
        return new CombinedOauth2Token(parent);
    }

    public static CombinedOauth2Token from(Oauth2Token parent, Map<String, Object> additionalInfo) {
        return new CombinedOauth2Token(parent, additionalInfo);
    }

    public static abstract class CombinedOauth2TokenBuilder<C extends CombinedOauth2Token, B extends CombinedOauth2TokenBuilder<C, B>> extends AbstractOauth2TokenBuilder<C, B> {
        /**
         * Add value to  additionalInfo only if optional is presented
         *
         * @param key      - key specified with value
         * @param optional - optional with value or empty
         * @return - builder
         */
        public CombinedOauth2TokenBuilder<C, B> addInfoIfPresent(String key, Optional<?> optional) {
            optional.ifPresent(x -> addInfo(key, optional.get()));
            return this;
        }
    }
}
