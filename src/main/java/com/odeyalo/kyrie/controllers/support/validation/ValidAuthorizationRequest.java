package com.odeyalo.kyrie.controllers.support.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation is used to mark parameter as required for validation.
 * Annotation is only used to annotate the {@link com.odeyalo.kyrie.core.authorization.AuthorizationRequest} and does not support other classes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValidAuthorizationRequest {
}
