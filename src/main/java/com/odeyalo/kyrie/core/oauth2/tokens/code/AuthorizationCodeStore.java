package com.odeyalo.kyrie.core.oauth2.tokens.code;

/**
 * Used to store an generated authorization code
 */
public interface AuthorizationCodeStore {

    /**
     * Save authorization code by id
     * @param id - code id
     * @param code - authorization code to save
     */
    void save(String id, AuthorizationCode code);

    /**
     * Find authorization code by ID
     * @param id - id that will be used to search
     * @return - authoriztion code that was found or null
     */
    AuthorizationCode findById(String id);

    /**
     * Find authorization code by value
     * @param authCode - authorization code value
     * @return - authorization code or null
     */
    AuthorizationCode findByAuthorizationCodeValue(String authCode);

    /**
     * Delete an authorization code from store by id
     * @param id - code id
     */
    void delete(String id);

    /**
     * Delete by AuthorizationCode
     * @param code - code to delete
     */
    void delete(AuthorizationCode code);
}
