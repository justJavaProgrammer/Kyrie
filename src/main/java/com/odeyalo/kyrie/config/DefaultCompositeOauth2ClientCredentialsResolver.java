package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.config.support.Oauth2ClientCredentialsResolverHelper;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * {@link Oauth2ClientCredentialsResolver} implementation that supports Oauth2 client authentication
 * through Basic Authentication, request parameters and JSON body.
 * <p>The parameters that will be used to perform authentication</p>
 * <ul>
 *     <li>client_id</li>
 *     <li>client_secret</li>
 * </ul>
 */
public class DefaultCompositeOauth2ClientCredentialsResolver implements Oauth2ClientCredentialsResolver {
    private final List<Oauth2ClientCredentialsResolverHelper> resolvers;

    public DefaultCompositeOauth2ClientCredentialsResolver(List<Oauth2ClientCredentialsResolverHelper> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Oauth2ClientCredentials resolveCredentials(HttpServletRequest request, boolean requireClientSecret) {
        Optional<Oauth2ClientCredentialsResolverHelper> optional = resolvers.stream().filter(resolver -> resolver.canBeResolved(request)).findFirst();
        if (optional.isEmpty()) {
            return null;
        }
        return optional.get().resolveCredentials(request);
    }
}
