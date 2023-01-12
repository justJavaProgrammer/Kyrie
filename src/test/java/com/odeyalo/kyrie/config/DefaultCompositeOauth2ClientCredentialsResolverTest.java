package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultCompositeOauth2ClientCredentialsResolverTest {
    DefaultCompositeOauth2ClientCredentialsResolver helper = new DefaultCompositeOauth2ClientCredentialsResolver(List.of());

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
