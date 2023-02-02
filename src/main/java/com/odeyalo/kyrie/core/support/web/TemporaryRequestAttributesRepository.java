package com.odeyalo.kyrie.core.support.web;

import javax.servlet.http.HttpServletRequest;

/**
 * The repository that used to store the temporary request attributes, provides the functionality to share the attributes between different requests, threads, etc.
 */
public interface TemporaryRequestAttributesRepository {
    /**
     * Save the value in repository with specific key
     * @param request - current request
     * @param key - key to associate the value
     * @param value - value to save
     */
    void save(HttpServletRequest request, String key, Object value);

    /**
     * Save the value in repository, but generate the key by itself
     * @param request - current request
     * @param value - value to save
     */
    void save(HttpServletRequest request, Object value);

    /**
     * Return the value associated with the given key
     * @param key - key associated with value
     * @return - value associated with the key, null otherwise
     */
    Object get(HttpServletRequest request, String key);

    /**
     * Return the value associated with the given key and cast the value to specific class
     * @param request - current request
     * @param key - associated with value
     * @param cls - class to cast the value
     * @param <T> - class to cast the value
     * @return - casted class associated with the value
     * @throws ClassCastException - if the cast cannot be performed
     */
    <T> T get(HttpServletRequest request, String key, Class<T> cls);

    /**
     * Return the value with the same class type
     * @param request - current request
     * @param cls - class type
     * @param <T> - type of the class
     * @return - the value from repository with the same class type
     */
    <T> T get(HttpServletRequest request, Class<T> cls);

    /**
     * Remove the value associated with the key
     * @param key - key associated with value
     */
    void remove(HttpServletRequest request, String key);

    /**
     * Remove the value from the repository
     * NOTE: In most cases the deletion is O(n) and {@link #remove(HttpServletRequest, String)} is more preferred
     * @param clsToRemove - class to remove
     */
    <T> void remove(HttpServletRequest request, Class<T> clsToRemove);

    /**
     * Clear the all request attributes associated with this request
     * @param request - request to remove the attributes
     */
    void clear(HttpServletRequest request);
}
