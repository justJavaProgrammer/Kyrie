package com.odeyalo.kyrie.controllers.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation that binds a method parameter or method return value to a named model attribute, exposed to a web view.
 * Supported for controller classes with @RequestMapping methods.
 * Same as ModelAttribute annotation, but supports the additional features like annotation fields binding
 * @see AdvancedModelAttributeMethodProcessor
 * @see org.springframework.web.bind.annotation.ModelAttribute
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AdvancedModelAttribute {}
