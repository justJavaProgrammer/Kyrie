package com.odeyalo.kyrie.config.support;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * Simple {@link HttpServletRequestWrapper} implementation that used to cache request body and request parameters from request, override some methods from parent class that return cached values.
 *
 * Similar to {@link org.springframework.web.util.ContentCachingRequestWrapper} but override also 'getInputStream' and 'getReader' methods.
 * @see HttpServletRequestWrapper
 * @see org.springframework.web.util.ContentCachingRequestWrapper
 * @see Request2CachedContentHttpServletRequestWrapperFilter
 */
public class CachedContentHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] cachedBody;
    private final Map<String, String[]> parameters;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     * @throws IOException if the request contains malformed body
     */
    public CachedContentHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.parameters = request.getParameterMap();
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }


    public byte[] getContentAsByteArray() {
        return cachedBody;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedContentServletInputStream(cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream array = new ByteArrayInputStream(cachedBody);
        return new BufferedReader(new InputStreamReader(array));
    }

    @Override
    public String getParameter(String name) {
        return parameters.containsKey(name) ? parameters.get(name)[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    public static class CachedContentServletInputStream extends ServletInputStream {

        private final InputStream cachedInputStream;

        public CachedContentServletInputStream(byte[] body) {
            this.cachedInputStream = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedInputStream.available() > 0;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }

        @Override
        public int read() throws IOException {
            return cachedInputStream.read();
        }
    }
}
