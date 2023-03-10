package com.odeyalo.kyrie.core.oauth2.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Oauth2Client that registered in Kyrie and can obtain oauth2 tokens.
 */
@AllArgsConstructor
@Builder
@Data
public class Oauth2Client implements UserDetails {
    /**
     * Unique id for this client
     */
    private final String clientId;
    /**
     * Client secret for this client that will be used to verify client
     */
    private final String clientSecret;
    /**
     * Set of allowed redirect uris provided by client.
     * If request contains redirect uri that does not contains in allowedRedirectUris, then request must be rejected
     */
    @Singular("allowedRedirectUri")
    private final Set<String> allowedRedirectUris;
    private final ClientType clientType;

    public void addAllowedRedirectUri(String redirectUri) {
        this.allowedRedirectUris.add(redirectUri);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return clientSecret;
    }

    @Override
    public String getUsername() {
        return clientId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Oauth2Client{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret=[PROTECTED]'" + '\'' +
                ", allowedRedirectUris=" + allowedRedirectUris +
                ", clientType=" + clientType +
                '}';
    }

    /**
     * Type of the client.
     *
     * Confidential clients are applications that are able to securely authenticate with the authorization server, for example being able to keep their registered client secret safe.
     * Public clients are unable to use registered client secrets, such as applications running in a browser or on a mobile device.
     */
    public enum ClientType {
        PUBLIC,
        CONFIDENTIAL
    }
}
