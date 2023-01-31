package com.odeyalo.kyrie.support.html;

import org.springframework.ui.Model;

/**
 * The interface is used to enhance the Model that will be used to render view.
 */
@FunctionalInterface
public interface ModelEnhancerPostProcessor {

    /**
     * Enhance the Model with parameters
     * @param templateType - type of the template that will be returned as response
     * @param model - model to enhance
     */
    void enhanceModel(String templateType, Model model);

}
