package com.odeyalo.kyrie.core.oauth2.tokens.refresh;

import com.odeyalo.kyrie.core.oauth2.RefreshToken;

/**
 * Simple repository that provides basic CRUD operations for {@link RefreshToken}
 */
public interface RefreshTokenRepository {
    /**
     * Save the {@link RefreshToken} and associate it with given id
     * @param id - unique token id
     * @param token - token to save
     */
    void save(String id, RefreshToken token);

    /**
     * Return the RefreshToken that was found, null if token with given id does not exist
     * @param id - id that will be used to search
     * @return - RefreshToken that was found, null otherwise
     */
    RefreshToken findById(String id);

    /**
     * Return the RefreshToken that was found, null if token with given token value does not exist
     * @param tokenValue- token value that will be used to search
     * @return - RefreshToken that was found, null otherwise
     */
    RefreshToken findByTokenValue(String tokenValue);

    /**
     * Update the old token with new RefreshToken.
     * @param oldTokenId - token id that will be updated
     * @param newToken - new token data
     */
    void update(String oldTokenId, RefreshToken newToken);

    /**
     * Delete the refresh token by id
     * @param id - id to delete
     */
    void deleteById(String id);

}
