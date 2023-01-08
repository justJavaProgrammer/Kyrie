package com.odeyalo.kyrie.config.configurers;

import lombok.Data;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Configurer that can be used to configure the views provided by Kyrie.
 */
@Data
public class Oauth2ServerViewRegistry {
    private final Map<String, View> views;

    public Oauth2ServerViewRegistry() {
        this.views = new HashMap<>();
    }

    /**
     * Add the view by type
     * @param type - type of the view. Type is specific key according to which you can access the view
     * @param view - view to registry
     */
    public void addView(String type, View view) {
        views.put(type, view);
    }

    /**
     * Remove view from container.
     * @param view - view to delete
     */
    public void removeView(View view) {
        views.values().removeIf(value -> value.equals(view));
    }

    /**
     * Remove view from container.
     * @param templateType - key that will be used to delete
     */
    public void removeView(String templateType) {
        views.remove(templateType);
    }

    /**
     * Size of the views presented in views container
     * @return - size of the views
     */
    public int size() {
        return views.size();
    }

    /**
     * Returns true if views container does not contain elements.
     * @return - true if container is empty, false otherwise
     */
    public boolean isEmpty() {
        return views.isEmpty();
    }

    /**
     * Returns true if views container contains the specified element by template type.
     * @param templateType - type of the template, key
     * @return - true if view container contains element, false otherwise
     */
    public boolean contains(String templateType) {
        return views.containsKey(templateType);
    }
}
