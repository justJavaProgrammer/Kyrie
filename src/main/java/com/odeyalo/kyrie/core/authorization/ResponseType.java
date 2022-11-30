package com.odeyalo.kyrie.core.authorization;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represent the response type
 * @see {https://www.rfc-editor.org/rfc/rfc6749#section-3.1.1}
 */
public enum ResponseType {
    CODE(true, "code"),
    TOKEN(false, "token");

    private final boolean serverScope;
    private final String simplifiedName;


    public static final Map<String,ResponseType> ALL_TYPES =
            Map.copyOf(Arrays.stream(values())
                    .collect(Collectors.toMap(responseType -> responseType.simplifiedName, st -> st)));


    ResponseType(boolean serverScope, String simplifiedName) {
        this.serverScope = serverScope;
        this.simplifiedName = simplifiedName;
    }

    public boolean isServerScope() {
        return serverScope;
    }

    public String getSimplifiedName() {
        return simplifiedName;
    }
}
