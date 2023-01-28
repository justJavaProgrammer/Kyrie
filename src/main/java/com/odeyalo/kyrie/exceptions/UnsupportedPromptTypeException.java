package com.odeyalo.kyrie.exceptions;

/**
 * Exception to throw when the prompt type provided by client is not supported
 */
public class UnsupportedPromptTypeException extends Oauth2Exception {

    public UnsupportedPromptTypeException(String message) {
        super(message, message, new Oauth2ErrorType("unsupported_prompt_type"));
    }

    public UnsupportedPromptTypeException(String message, String description) {
        super(message, description, new Oauth2ErrorType("unsupported_prompt_type"));
    }
}
