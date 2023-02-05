package com.odeyalo.kyrie.core.support.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * {@link TemporaryRequestAttributesRepository} implementation that stores the attributes in http session.
 *
 * <p>
 * The ALL values will be wrapped in {@link HttpSessionAttributeWrapper}
 * to make possible see the difference between default session attributes and attributes from HttpSessionTemporaryRequestAttributesRepository
 * </p>
 */
public class HttpSessionTemporaryRequestAttributesRepository implements TemporaryRequestAttributesRepository {
    private final Logger logger = LoggerFactory.getLogger(HttpSessionTemporaryRequestAttributesRepository.class);

    @Override
    public void save(HttpServletRequest request, String key, Object value) {
        Assert.notNull(value, "The value must be not null!");
        request.getSession().setAttribute(key, new HttpSessionAttributeWrapper(value));
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
        Object attribute = request.getSession().getAttribute(key);

        if (attribute instanceof HttpSessionAttributeWrapper) {
            HttpSessionAttributeWrapper wrapper = (HttpSessionAttributeWrapper) attribute;
            return wrapper.getValue();
        }
        return attribute;
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
        // Unwrap the attribute if necessary
        if (attribute instanceof HttpSessionAttributeWrapper) {
            HttpSessionAttributeWrapper wrapper = (HttpSessionAttributeWrapper) attribute;
            attribute = wrapper.getValue();
        }

        return cls.cast(attribute);
    }

    @Override
    public void remove(HttpServletRequest request, String key) {
        if (key == null) {
            return;
        }
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

    /**
     * The clear() method implementation that clears ONLY the {@link HttpSessionAttributeWrapper} instances and does not affect on other session attributes
     * @param request - request to remove the attributes
     */
    @Override
    public void clear(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            if (session.getAttribute(key) instanceof HttpSessionAttributeWrapper) {
                session.removeAttribute(key);
            }
        }
    }

    private <T> String getKeyByClass(Class<T> cls, HttpSession session) {
        Iterator<String> iterator = session.getAttributeNames().asIterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object attribute = session.getAttribute(key);

            if (attribute instanceof HttpSessionAttributeWrapper) {
                attribute = ((HttpSessionAttributeWrapper) attribute).getValue();
            }

            if (attribute.getClass().equals(cls)) {
                return key;
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    private static class HttpSessionAttributeWrapper {
        private Object value;
    }
}
