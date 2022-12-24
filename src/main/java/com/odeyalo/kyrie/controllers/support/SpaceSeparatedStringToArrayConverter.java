package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.support.AdvancedStringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

/**
 * Convert a string to an array by SPACE split
 * @version 1.0
 */
public class SpaceSeparatedStringToArrayConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;

    public SpaceSeparatedStringToArrayConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.isAssignableTo(TypeDescriptor.valueOf(String.class)) && targetType.isArray();
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Object[].class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        // Split without check since we are already check that object is String and targetType is array in 'matches' method
        String list = (String) source;
        String[] elements = AdvancedStringUtils.spaceDelimitedListToStringArray(list);
        TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
        Assert.notNull(targetElementType, "elementTypeDescriptor cannot be null!");
        Object result = Array.newInstance(targetElementType.getType(), elements.length);
        // Iterate through the elements, convert it to target type and push it to result array
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i];
            Object convertedValue = conversionService.convert(element.trim(), sourceType, targetElementType);
            Array.set(result, i, convertedValue);
        }
        return result;
    }
}
