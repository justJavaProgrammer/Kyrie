package com.odeyalo.kyrie.exceptions;

/**
 * Exception to throw when the prompt type provided by client is not supported
 */
public class UnsupportedPromptTypeException extends RedirectUriAwareOauth2Exception {

    public UnsupportedPromptTypeException(String message, String redirectUri) {
        super(message, message, redirectUri, new Oauth2ErrorType("unsupported_prompt_type"));
    }

    public UnsupportedPromptTypeException(String message, String description, String redirectUri) {
        super(message, description, redirectUri, new Oauth2ErrorType("unsupported_prompt_type"));
    }
}
