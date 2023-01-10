package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultAccessTokenGranterStrategyFactory implements AccessTokenGranterStrategyFactory {
    private final Map<AuthorizationGrantType, AccessTokenGranterStrategy> granters;


    public DefaultAccessTokenGranterStrategyFactory(List<AccessTokenGranterStrategy> granters) {
        this.granters = granters.stream().collect(Collectors.toMap(AccessTokenGranterStrategy::grantType, Function.identity()));
    }

    @Override
    public AccessTokenGranterStrategy getGranter(TokenRequest request) {
        AuthorizationGrantType grantType = request.getGrantType();
        return granters.get(grantType);
    }
}
