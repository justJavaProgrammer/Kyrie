package com.odeyalo.kyrie.core.oauth2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represent client-application credentials
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3.4">Client credentials</a>
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Oauth2ClientCredentials {
    private String clientId;
    private String clientSecret;

    public Oauth2ClientCredentials(String clientId) {
        this.clientId = clientId;
    }

    public static Oauth2ClientCredentials of(String clientId, String clientSecret) {
        return new Oauth2ClientCredentials(clientId, clientSecret);
    }

    public static Oauth2ClientCredentials of(String clientId) {
        return new Oauth2ClientCredentials(clientId);
    }
}
