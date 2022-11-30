package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.controllers.support.AdvancedModelAttributeMethodProcessor;
import com.odeyalo.kyrie.controllers.support.String2AuthorizationGrantTypeConverter;
import com.odeyalo.kyrie.controllers.support.String2ResponseTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web mvc configuration that add custom resource handlers, arguments resolvers, etc
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AdvancedModelAttributeMethodProcessor(new ModelAttributeMethodProcessor(false)));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new String2AuthorizationGrantTypeConverter());
        registry.addConverter(new String2ResponseTypeConverter());
    }
}