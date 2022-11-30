package com.odeyalo.kyrie.core.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic info about oauth2 user
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Oauth2UserAuthenticationInfo {
    private String username;
    private String password;
}
