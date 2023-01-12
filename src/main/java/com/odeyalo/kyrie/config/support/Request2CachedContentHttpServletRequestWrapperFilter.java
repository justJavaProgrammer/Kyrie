package com.odeyalo.kyrie.config.support;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Simple filter that used to wrap current request to {@link CachedContentHttpServletRequestWrapper}
 */
public class Request2CachedContentHttpServletRequestWrapperFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        CachedContentHttpServletRequestWrapper cachingRequestWrapper = new CachedContentHttpServletRequestWrapper(request);
        filterChain.doFilter(cachingRequestWrapper, response);
    }
}
