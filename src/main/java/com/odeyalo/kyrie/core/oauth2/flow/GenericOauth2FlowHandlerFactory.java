package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Produces any Oauth2FlowHandler that already cached based on grant type.
 * @version 1.0
 */
@Component
public class GenericOauth2FlowHandlerFactory implements Oauth2FlowHandlerFactory {
    private final Map<String, Oauth2FlowHandler> cache;

    public GenericOauth2FlowHandlerFactory(List<Oauth2FlowHandler> handlers) {
        this.cache = handlers.stream().collect(Collectors.toMap(Oauth2FlowHandler::getFlowName, Function.identity()));
    }

    @Override
    public Oauth2FlowHandler getOauth2FlowHandler(AuthorizationRequest request) {
        return cache.get(request.getGrantType().getGrantName());
    }
}
