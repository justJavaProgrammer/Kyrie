package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.oauth2.prompt.PromptType;
import com.odeyalo.kyrie.exceptions.UnsupportedPromptTypeException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class String2PromptTypeConverter implements HandlerMethodArgumentResolver{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(PromptType.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String prompt = webRequest.getParameter("prompt");
        PromptType promptType = PromptType.valueOf(prompt);
        if (promptType == null) {
            throw new UnsupportedPromptTypeException(String.format("The prompt type: %s does not supported", prompt));
        }
        return promptType;
    }
}
