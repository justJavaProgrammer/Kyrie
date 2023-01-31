package com.odeyalo.kyrie.config.configurers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Configurer that will be used to configure the Kyrie available endpoints.
 */
@Data
@Accessors(chain = true)
public class Oauth2ServerEndpointsConfigurer {
    /**
     * Used to specify the prefix that will be used for login and authorize endpoints only.
     * Prefix does not applied to token and token info endpoints
     */
    @NonNull
    private String prefix = "/oauth2";
    @NonNull
    private String loginEndpointName = prefix + "/login";
    @NonNull
    private String tokenEndpointName = "/token";
    @NonNull
    private String tokenInfoEndpointName = "/tokeninfo";
    @NonNull
    private String authorizeEndpointName = prefix + "/authorize";
    @NonNull
    private String consentPageEndpointName = prefix + "/consent";
    /**
     * Build Oauth2ServerEndpointsInfo based on provided values
     *
     * @return - ready-to-use Oauth2ServerEndpointsInfo
     */
    public Oauth2ServerEndpointsInfo buildOauth2ServerEndpointsInfo() {
        return new Oauth2ServerEndpointsInfo(prefix, loginEndpointName, tokenEndpointName, tokenInfoEndpointName, authorizeEndpointName, consentPageEndpointName);
    }

    /**
     * Data class that used to set and store endpoints names
     */
    @Data
    @AllArgsConstructor
    public static class Oauth2ServerEndpointsInfo {
        private String prefix;
        private String loginEndpointName;
        private String tokenEndpointName;
        private String tokenInfoEndpointName;
        private String authorizeEndpointName;
        private String consentPageEndpointName;
    }
}
