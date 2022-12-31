package com.odeyalo.kyrie.core.oauth2.client;

/**
 * Repository that holds the Oauth2 clients instances.
 * @see Oauth2Client
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-2">Client Registration</a>
 */
public interface Oauth2ClientRepository {

    /**
     * Find oauth2 client by client id and return it.
     * If client was not found null will be returned
     * @param clientId - client id to search user
     * @return - Oauth2Client associated with this client id
     */
    Oauth2Client findOauth2ClientById(String clientId);
}
