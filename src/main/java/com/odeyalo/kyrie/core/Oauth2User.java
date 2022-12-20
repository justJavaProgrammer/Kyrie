package com.odeyalo.kyrie.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * Generic entity in Kyrie. Represent user that already log in.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2User {
    private String id;
    private String username;
    private String password;
    private Set<String> authorities;
    // Additional info about user. Can store email address and other useful info about user.
    // Key - name of info part
    // Value - value of info part
    private Map<String, Object> additionalInfo;
}
