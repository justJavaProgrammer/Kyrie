package com.odeyalo.kyrie.core.oauth2.prompt;

public interface PromptHandlerFactory {

    PromptHandler getHandler(PromptType promptType);

    default PromptHandler getHandler(String promptName) {
        return getHandler(PromptType.valueOf(promptName));
    }
}
