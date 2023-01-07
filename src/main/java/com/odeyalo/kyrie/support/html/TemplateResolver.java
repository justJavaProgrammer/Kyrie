package com.odeyalo.kyrie.support.html;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Interface that used to resolve the html template name that must be returned to client as response.
 *
 * @version 1.0
 */
public interface TemplateResolver {

    /**
     * Returns template by template type.
     * @param templateType - type of the template
     * @return - ModelAndView that contains only view and empty Model
     */
    default ModelAndView getTemplate(String templateType) {
        return getTemplate(templateType, new ExtendedModelMap());
    }

    /**
     * Resolve the html template by given template type, add dynamic values from Model in View
     * @param templateType - type of the template
     * @return - ModelAndView that will be returned as response by template type with injected values from Model, otherwise null
     */
    ModelAndView getTemplate(String templateType, Model model);

    /**
     * Registry static view with specific template type(used as key)
     * @param templateType - type of the template
     * @param view - ModelAndView that contains view with optional model
     */
    void addTemplate(String templateType, View view);
}
