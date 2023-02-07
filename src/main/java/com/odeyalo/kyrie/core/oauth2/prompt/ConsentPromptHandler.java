package com.odeyalo.kyrie.core.oauth2.prompt;

import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.sso.RememberedLoggedUserAccountsContainer;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.odeyalo.kyrie.support.html.DefaultTemplateResolver.CONSENT_VIEW_TEMPLATE_TYPE;

/**
 * {@link PromptHandler} implementation that used to handle the 'consent' prompt.
 * <p>
 *     User consent represents a user's explicit permission to allow an application to access resources protected by scopes.
 * </p>
 */
public class ConsentPromptHandler implements PromptHandler {
    private final TemplateResolver templateResolver;
    private final RememberMeService rememberMeService;

    public ConsentPromptHandler(TemplateResolver templateResolver, RememberMeService rememberMeService) {
        this.templateResolver = templateResolver;
        this.rememberMeService = rememberMeService;
    }

    @Override
    public ModelAndView handlePrompt(Model model, HttpServletRequest request, HttpServletResponse response) {
        RememberedLoggedUserAccountsContainer container = rememberMeService.autoLogin(request);
        return handlePrompt(model, container, request, response);
    }

    @Override
    public ModelAndView handlePrompt(Model model, RememberedLoggedUserAccountsContainer container, HttpServletRequest request, HttpServletResponse response) {
        return templateResolver.getTemplate(CONSENT_VIEW_TEMPLATE_TYPE);
    }

    @Override
    public PromptType getType() {
        return PromptType.CONSENT;
    }
}
