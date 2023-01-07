package com.odeyalo.kyrie.support.html;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default TemplateResolver implementation that uses Map to store views.
 *
 * @see TemplateResolver
 * @version 1.0
 */
public class DefaultTemplateResolver implements TemplateResolver {
    /**
     * Constant to registry html template that will be returned for /login request
     */
    public static final String LOGIN_TEMPLATE_TYPE = "LOGIN_TEMPLATE";

    private final Map<String, View> templates;
    private final List<ModelEnhancerPostProcessor> enhancers;

    /**
     * Create an empty DefaultTemplateResolver
     */
    public DefaultTemplateResolver() {
        this.templates = new HashMap<>();
        this.enhancers = new ArrayList<>();
    }

    public DefaultTemplateResolver(List<ModelEnhancerPostProcessor> enhancers) {
        this.templates = new HashMap<>();
        this.enhancers = enhancers;
    }

// TODO: TemplateResolver must return templates with values from model. But how to make injection if controller returns empty model? Should I create interceptors?
    /**
     * Create DefaultTemplateResolver with specified templates
     * @param templates - templates to registry
     * @param enhancers
     */
    public DefaultTemplateResolver(Map<String, View> templates, List<ModelEnhancerPostProcessor> enhancers) {
        this.templates = templates;
        this.enhancers = enhancers;
    }

    @Override
    public ModelAndView getTemplate(String templateType, Model model) {
        View view = this.templates.get(templateType);
        if (view == null) {
            return null;
        }

        enhancers.forEach(enhancer -> enhancer.enhanceModel(templateType, model));

        return new ModelAndView(view, model.asMap());
    }

    @Override
    public void addTemplate(String templateType, View view) {
        this.templates.put(templateType, view);
    }
}
