package com.odeyalo.kyrie.controllers.support;

import java.lang.annotation.*;


/**
 * Annotation to map the multipart/form-data content type to Java Objects.
 * It helpful when the form data properties is in snake_case and Java Object is in camelCase
 * @see AdvancedModelAttributeMethodProcessor
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormProperty  {
    String value();
}
