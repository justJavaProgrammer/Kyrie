package com.odeyalo.kyrie.controllers.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * HandlerMethodArgumentResolver to resolve {@see AdvancedModelAttribute} annotation to provide more functionality to ModelAttribute data binding.
 * Since ModelAttribute does not support custom property name and resolve parameter by field's name,
 * added AdvancedModelAttribute and FormProperty annotations.
 *
 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver
 * @see ModelAttributeMethodProcessor
 * @see FormProperty
 */
public class AdvancedModelAttributeMethodProcessor implements HandlerMethodArgumentResolver {
    private final ModelAttributeMethodProcessor modelAttributeMethodProcessor;
    private final Logger logger = LoggerFactory.getLogger(AdvancedModelAttributeMethodProcessor.class);


    /**
     * Map of parsers to convert primitive and wrappers from String
     * <p>
     *     Key - Type of the class.
     * </p>
     * <p>
     *     Value - Function that parse String to required type
     * </p>
     * @see Function
     */
    private final Map<Class<?>, Function<String, ?>> parsers = new HashMap<>();

    {
        parsers.put(int.class, Integer::parseInt);
        parsers.put(long.class, Long::parseLong);
        parsers.put(byte.class, Byte::parseByte);
        parsers.put(short.class, Short::parseShort);
        parsers.put(double.class, Double::parseDouble);
        parsers.put(boolean.class, Boolean::parseBoolean);
        parsers.put(char.class, parseCharacter());

        parsers.put(String.class, String::valueOf);
        parsers.put(Integer.class, Integer::parseInt);
        parsers.put(Long.class, Long::parseLong);
        parsers.put(Byte.class, Byte::parseByte);
        parsers.put(Short.class, Short::parseShort);
        parsers.put(Double.class, Double::parseDouble);
        parsers.put(Boolean.class, Boolean::parseBoolean);
        parsers.put(Character.class, parseCharacter());
    }

    /**
     * Parse String to Character. If source is null or length is not 1 then Character.MIN_VALUE will be returned
     *
     * @return - first character of the source
     * @see Character
     */
    private Function<String, Character> parseCharacter() {
        return (source) -> {
            if (source == null || source.length() != 1) {
                return Character.MIN_VALUE;
            }
            return source.charAt(0);
        };
    }


    public AdvancedModelAttributeMethodProcessor(ModelAttributeMethodProcessor modelAttributeMethodProcessor) {
        this.modelAttributeMethodProcessor = modelAttributeMethodProcessor;
    }

    /**
     * True if the parameter has AdvancedModelAttribute annotation
     *
     * @param parameter – the method parameter to check
     * @return True if the parameter has AdvancedModelAttribute annotation
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AdvancedModelAttribute.class);
    }

    /**
     * Resolve the parameter using ModelAttributeMethodProcessor.
     * Add FormProperty annotation processing to bind the form-data by custom name(Or by field name if the FormProperty annotation is not set)
     *
     * @see ModelAttributeMethodProcessor
     * @see FormProperty
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Assert.state(mavContainer != null, "ModelAttributeMethodProcessor requires ModelAndViewContainer");
        Assert.state(binderFactory != null, "ModelAttributeMethodProcessor requires WebDataBinderFactory");

        String name = ModelFactory.getNameForParameter(parameter);
        mavContainer.setBindingDisabled(name);

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object argument = modelAttributeMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        doAnnotationProcessing(request, argument);
        return argument;
    }

    /**
     * Process the FormProperty annotation. Resolve parameters from request and set it to argument
     *
     * @param request  - request to resolve parameters
     * @param argument - original object to set fields
     */
    protected void doAnnotationProcessing(HttpServletRequest request, Object argument) {
        Field[] fields = argument.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String key = getParameterNameByFormProperty(field);
            this.logger.debug("Using '{}' as key for field: {} for class: {}", key, field.getName(), argument);
            resolveAndSet(request, argument, field, key);
        }
    }

    /**
     * Get parameter name by FormProperty data and return it.
     *
     * @param field - field to resolve annotation
     * @return name of parameter from FormProperty annotation
     * @see FormProperty
     */
    protected String getParameterNameByFormProperty(Field field) {
        return field.isAnnotationPresent(FormProperty.class) && isAnnotationValueNonEmpty(field) ?
                field.getAnnotation(FormProperty.class).value() :
                field.getName();
    }

    /**
     * * True if FormProperty value annotation is null or empty
     *
     * @param field - field to check annotation
     * @return - true if FormProperty value annotation is null or empty
     * @see FormProperty
     */
    protected boolean isAnnotationValueNonEmpty(Field field) {
        return !field.getAnnotation(FormProperty.class).value().equals(FormProperty.EMPTY_VALUE);
    }


    /**
     * Resolve parameter from request and set it to the field. If field cannot be set null or default value will be set as value.
     * If parameter is not presented in request default value will be used.
     *
     * @param request  - request to get parameters
     * @param argument - original object to set fields
     * @param field    - field to set value
     * @param key      - key of parameter name
     */
    protected void resolveAndSet(HttpServletRequest request, Object argument, Field field, String key) {
        FormProperty annotation = field.getAnnotation(FormProperty.class);
        String resolvedParameter = resolveParameterFromRequest(request, key);
        // If parameter is null and annotation contains defaultValue then set field value to defaultValue
        if (resolvedParameter == null && (annotation != null && !annotation.defaultValue().equals(ValueConstants.DEFAULT_NONE))) {
            setField(field, argument, annotation.defaultValue());
        } else {
            setField(field, argument, resolvedParameter);
        }
    }

    /**
     * Resolve parameter from request and return it
     *
     * @param request       - request with parameters
     * @param parameterName - parameter name to resolve value
     * @return - value of parameter
     */
    protected String resolveParameterFromRequest(HttpServletRequest request, String parameterName) {
        return request.getParameter(parameterName);
    }

    /**
     * Set field value. Supports null as field value.
     * If field type is primitive then method will auto wrap it to required type
     *
     * @param field      - field to set value
     * @param object     - original object to set field
     * @param fieldValue - field value to set
     */
    protected void setField(Field field, Object object, String fieldValue) {
        if (fieldValue == null) {
            return;
        }
        Class<?> type = field.getType();
        Function<String, ?> parser = parsers.get(type);
        if (parser == null) {
            this.logger.debug("Parser is null and field can't be set");
            return;
        }
        Object apply = parser.apply(fieldValue);
        ReflectionUtils.setField(field, object, apply);
        this.logger.debug("Set the field {} with value: {}", field.getName(), fieldValue);
    }
}
