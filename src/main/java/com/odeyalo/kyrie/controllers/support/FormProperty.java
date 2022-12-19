package com.odeyalo.kyrie.controllers.support;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to map the multipart/form-data content type to Java Objects.
 * It helpful when the form data properties is in snake_case and Java Object is in camelCase
 *
 * @see AdvancedModelAttributeMethodProcessor
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormProperty {
    String EMPTY_VALUE = "";

    /**
     * Name of parameter in Form from request.
     * If value is empty or not provided will be used field name
     * @return - name of parameter in form
     */
    String value() default EMPTY_VALUE;

    /**
     * Parameter that will be used if value annotation is not presented in request
     *
     * @return - default value if value is not presented in request
     */
    String defaultValue() default ValueConstants.DEFAULT_NONE;
}
