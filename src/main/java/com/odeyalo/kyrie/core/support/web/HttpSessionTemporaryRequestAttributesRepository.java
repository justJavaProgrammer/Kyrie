package com.odeyalo.kyrie.core.support.web;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * {@link TemporaryRequestAttributesRepository} implementation that stores the attributes in http session
 */
public class HttpSessionTemporaryRequestAttributesRepository implements TemporaryRequestAttributesRepository {
    private final Logger logger = LoggerFactory.getLogger(HttpSessionTemporaryRequestAttributesRepository.class);

    @Override
    public void save(HttpServletRequest request, String key, Object value) {
        Assert.notNull(value, "The value must be not null!");
        request.getSession().setAttribute(key, value);
        this.logger.info("Saved the request attribute in http session with key: {} and value: {}", key, value);
    }

    @Override
    public void save(HttpServletRequest request, Object value) {
        Assert.notNull(value, "The value must be not null!");
        String key = value.getClass().getSimpleName() + RandomStringUtils.randomAlphanumeric(5);
        save(request, key, value);
    }

    @Override
    public Object get(HttpServletRequest request, String key) {
        return request.getSession().getAttribute(key);
    }

    @Override
    public <T> T get(HttpServletRequest request, String key, Class<T> cls) {
        Object o = get(request, key);
        return cls.cast(o);
    }

    @Override
    public <T> T get(HttpServletRequest request, Class<T> cls) {
        HttpSession session = request.getSession();
        String key = getKeyByClass(cls, session);
        Object attribute = session.getAttribute(key);
        return cls.cast(attribute);
    }

    @Override
    public void remove(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        session.removeAttribute(key);
        this.logger.info("Removed the attribute from request: {}", key);
    }

    @Override
    public <T> void remove(HttpServletRequest request, Class<T> clsToRemove) {
        HttpSession session = request.getSession();
        String key = getKeyByClass(clsToRemove, session);

        remove(request, key);
    }

    @Override
    public void clear(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            session.removeAttribute(key);
        }
    }

    private <T> String getKeyByClass(Class<T> cls, HttpSession session) {
        Iterator<String> iterator = session.getAttributeNames().asIterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object attribute = session.getAttribute(key);
            if (attribute.getClass().equals(cls)) {
                return key;
            }
        }
        return null;
    }
}
