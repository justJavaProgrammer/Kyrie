package com.odeyalo.kyrie.support;

import org.springframework.util.StringUtils;

public abstract class AdvancedStringUtils extends StringUtils {
    private static final String SPACE = " ";

    public static String[] spaceDelimitedListToStringArray(String list) {
        return delimitedListToStringArray(list, SPACE);
    }
}
