package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert incoming string to AuthorizationGrantType enum
 */
public class String2AuthorizationGrantTypeConverter implements Converter<String, AuthorizationGrantType> {

    @Override
    public AuthorizationGrantType convert(String source) {
        return AuthorizationGrantType.ALL_TYPES.get(source);
    }
}
