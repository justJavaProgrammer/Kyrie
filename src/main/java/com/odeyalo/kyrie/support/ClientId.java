package com.odeyalo.kyrie.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple wrapper class that uses to wrap client_id from request.
 * If request does not contains client_id, then null will be set as clientIdValue
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientId {
    private String clientIdValue;


    public static ClientId wrap(String clientIdValue) {
        return new ClientId(clientIdValue);
    }
}
