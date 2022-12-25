package com.odeyalo.kyrie.core;

import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * Generic entity in Kyrie. Represent user that already log in.
 */
@Data
@Builder
@AllArgsConstructor
public class Oauth2User {
    private String id;
    private String username;
    private String password;
    private Set<String> authorities;
    // Additional info about user. Can store email address and other useful info about user.
    // Key - name of info part
    // Value - value of info part
    @Singular("addInfo")
    private Map<String, Object> additionalInfo;
}
