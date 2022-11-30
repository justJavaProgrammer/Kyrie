package com.odeyalo.kyrie.controllers.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * HandlerMethodArgumentResolver to resolve {@see AdvancedModelAttribute} annotation to provide more functionality to ModelAttribute data binding.
 * Since ModelAttribute does not support custom property name and resolve parameter by field's name,
 * added AdvancedModelAttribute and FormProperty annotations.
 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver
 * @see ModelAttributeMethodProcessor
 * @see FormProperty
 */
public class AdvancedModelAttributeMethodProcessor implements HandlerMethodArgumentResolver {
    private final ModelAttributeMethodProcessor modelAttributeMethodProcessor;
    private final Logger logger = LoggerFactory.getLogger(AdvancedModelAttributeMethodProcessor.class);

    public AdvancedModelAttributeMethodProcessor(ModelAttributeMethodProcessor modelAttributeMethodProcessor) {
        this.modelAttributeMethodProcessor = modelAttributeMethodProcessor;
    }

    /**
     * True if the parameter has AdvancedModelAttribute annotation
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

    private void doAnnotationProcessing(HttpServletRequest request, Object argument) throws IllegalAccessException {
        Field[] fields = argument.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            field.setAccessible(true);
            if (field.isAnnotationPresent(FormProperty.class) && field.get(argument) == null) {
                FormProperty annotation = field.getAnnotation(FormProperty.class);
                String formKeyName = annotation.value();
                resolveAndSet(request, argument, field, fieldName, formKeyName);
            } else {
                resolveAndSet(request, argument, field, fieldName, fieldName);
            }
        }
    }

    private void resolveAndSet(HttpServletRequest request, Object argument, Field field, String fieldName, String parameterName) throws IllegalAccessException {
        String formDataValue = request.getParameter(parameterName);
        field.set(argument, formDataValue);
        this.logger.debug("Set the field {} with value: {}", fieldName, formDataValue);
    }
}
