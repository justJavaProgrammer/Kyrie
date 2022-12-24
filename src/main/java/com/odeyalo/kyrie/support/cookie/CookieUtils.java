package com.odeyalo.kyrie.support.cookie;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

/**
 * Utility class to working with cookie
 */
public class CookieUtils {

    private static final Logger logger = LoggerFactory.getLogger(CookieUtils.class);

    /**
     * Resolve cookie from request by name and wrap it to Optional
     * @param request - current request
     * @param name - name of cookie to resolve
     * @return - Optional with cookie or Optional.empty()
     */
    public static Optional<Cookie> getCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }

    /**
     * Add cookie to response.
     * NOTE: This class does not support custom cookie lifetime and set cookie that has no lifetime/
     * @param response - response that will be returned to client
     * @param name - name of the cookie that will be added to response
     * @param value - cookie value
     */
    public static void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * Add cookie to response. This method supports custom cookie lifetime in seconds
     * @param response - response that will be returned to client
     * @param name - name of the cookie that will be added to response
     * @param value - cookie value
     * @param maxAge - cookie lifetime in seconds
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * Delete cookie from request by name
     * @param request - request to resolve cookie
     * @param response - response without cookie
     * @param name - name of cookie to delete
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setValue("");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * Serialize object to Base64 encoding to make cookie more safer.
     * NOTE: Object MUST implement Serializable interface
     * @param object - object to serialize
     * @return - string representation of the serialized object in Base64 encoding
     */
    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * Deserialize cookie and cast it to specific class.
     * @param cookie - cookie to deserialize
     * @param cls - class that will be used to cast cookie value
     * @param <T> - type of class
     * @return - Deserialized and casted object
     * @throws ClassCastException - if content can't be casted to T
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        logger.info(cookie.getName() + " " + " " + cookie.getValue());
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
