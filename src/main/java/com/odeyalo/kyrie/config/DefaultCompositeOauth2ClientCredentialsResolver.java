package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.config.support.Oauth2ClientCredentialsResolverHelper;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * {@link Oauth2ClientCredentialsResolver} implementation that uses {@link Oauth2ClientCredentialsResolverHelper} to resolve client credentials.
 *
 * <p>
 *     Note: ONLY first matching {@link Oauth2ClientCredentialsResolverHelper} will be used, other implementations will be ignored.
 * </p>
 *
 * @see Oauth2ClientCredentialsResolverHelper
 * @see Oauth2ClientCredentialsResolver
 */
public class DefaultCompositeOauth2ClientCredentialsResolver implements Oauth2ClientCredentialsResolver {
    private final List<Oauth2ClientCredentialsResolverHelper> resolvers;

    public DefaultCompositeOauth2ClientCredentialsResolver(List<Oauth2ClientCredentialsResolverHelper> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Oauth2ClientCredentials resolveCredentials(HttpServletRequest request, boolean requireClientSecret) {
        return resolvers
                .stream()
                .filter(resolver -> resolver.canBeResolved(request))
                .findFirst()
                .map(resolver -> resolver.resolveCredentials(request))
                .orElse(null);
    }
}
