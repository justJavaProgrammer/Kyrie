package com.odeyalo.kyrie.core.oauth2.prompt;

import java.util.LinkedHashMap;
import java.util.Map;

public class PromptType {
    public static final PromptType NONE = new PromptType("none");
    public static final PromptType LOGIN = new PromptType("login");
    public static final PromptType SELECT_ACCOUNT = new PromptType("select_account");
    public static final PromptType CONSENT = new PromptType("consent");

    private final String promptName;

    private static Map<String, PromptType> cached;

    public PromptType(String promptName) {
        this.promptName = promptName;
        cachePrompt(promptName, this);
    }

    public static PromptType valueOf(String promptName) {
        if (promptName == null) {
            return null;
        }
        return cached.get(promptName);
    }

    public String getPromptName() {
        return promptName;
    }

    private static void cachePrompt(String name, PromptType type){
        if (cached == null) {
            cached = new LinkedHashMap<>();
            System.out.println("Create new cache");
        }
        cached.put(name, type);
    }
}
