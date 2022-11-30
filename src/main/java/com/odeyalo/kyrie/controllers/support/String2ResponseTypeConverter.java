package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.authorization.ResponseType;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert string to ResponseType
 */
public class String2ResponseTypeConverter implements Converter<String, ResponseType> {

    @Override
    public ResponseType convert(String source) {
        return ResponseType.ALL_TYPES.get(source);
    }
}
