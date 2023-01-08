package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultOauth2ClientCredentialsResolverHelperTest {
    DefaultOauth2ClientCredentialsResolverHelper helper = new DefaultOauth2ClientCredentialsResolverHelper();

    @Test
    void resolveCredentials() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String clientId = "name";
        request.addParameter("client_id", clientId);
        String clientSecret = "secret";
        request.addParameter("client_secret", clientSecret);
        Oauth2ClientCredentials clientCredentials = helper.resolveCredentials(request, true);
        assertEquals(clientId, clientCredentials.getClientId());
        assertEquals(clientSecret, clientCredentials.getClientSecret());
    }

    @Test
    void resolveCredentialsWithoutClientSecret() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String clientId = "name";
        request.addParameter("client_id", clientId);
        Oauth2ClientCredentials clientCredentials = helper.resolveCredentials(request, false);
        assertEquals(clientId, clientCredentials.getClientId());
        assertNull(clientCredentials.getClientSecret());
    }
}
