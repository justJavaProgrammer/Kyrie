package com.odeyalo.kyrie.core.oauth2.prompt;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimplePromptHandlerFactory implements PromptHandlerFactory {
    private final Map<PromptType, PromptHandler> handlers;

    public SimplePromptHandlerFactory(List<PromptHandler> handlers) {
        this.handlers = handlers.stream().collect(Collectors.toMap(PromptHandler::getType, Function.identity()));
    }

    public SimplePromptHandlerFactory(Map<PromptType, PromptHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public PromptHandler getHandler(PromptType promptType) {
        return handlers.get(promptType);
    }
}
