package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default ClientSideOauth2FlowHandlerFactory that produces only created implementations of
 * ClientSideOauth2FlowHandler type based on AuthorizationRequest grant type;
 * @see ClientSideOauth2FlowHandler
 * @see ClientSideOauth2FlowHandlerFactory
 * @see AuthorizationRequest
 * @version 1.0
 */
public class DefaultClientSideOauth2FlowHandlerFactory implements ClientSideOauth2FlowHandlerFactory {
    private final Map<String, ClientSideOauth2FlowHandler> handlers;
    private final Logger logger = LoggerFactory.getLogger(DefaultClientSideOauth2FlowHandlerFactory.class);

    public DefaultClientSideOauth2FlowHandlerFactory(List<ClientSideOauth2FlowHandler> handlers) {
        this.handlers = handlers.stream().collect(Collectors.toMap(Oauth2FlowHandler::getFlowName, Function.identity()));
        this.logger.info("Initialized the handlers map with: {}", handlers);
    }

    @Override
    public ClientSideOauth2FlowHandler getHandler(AuthorizationRequest request) {
        String grantType = request.getGrantType().getGrantName();
        return handlers.get(grantType);
    }
}
