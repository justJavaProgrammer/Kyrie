package com.odeyalo.kyrie.config.annotation;

import com.odeyalo.kyrie.config.configuration.KyrieOauth2Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that enables the Kyrie authorization server.
 * It simply import the configuration with all required beans and configure it.
 * @see KyrieOauth2Configuration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(value = KyrieOauth2Configuration.class)
public @interface EnableKyrieAuthorizationServer {}
