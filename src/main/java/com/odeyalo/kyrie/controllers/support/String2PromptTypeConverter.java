package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.oauth2.prompt.PromptType;
import org.springframework.core.convert.converter.Converter;

public class String2PromptTypeConverter implements Converter<String, PromptType> {

    /**
     * Convert string to {@link PromptType}, return null if convertion cannot be done or prompt type does not exist
     * @param source - string to convert
     * @return - PromptType based on the source, null otherwise
     */
    @Override
    public PromptType convert(String source) {
        return PromptType.valueOf(source);
    }
}
