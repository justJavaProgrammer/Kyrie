package com.odeyalo.kyrie.core.support;

/**
 * Main interface to validate anything using multiple steps
 * @param <T> - type of object to validation
 */
public interface ValidationStep<T> {
    /**
     * Validate object and return ValidationResult
     * @param obj - object to check
     * @return - ValidationResult with success or failed
     */
    ValidationResult validate(T obj);
}
