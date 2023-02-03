package com.odeyalo.kyrie.core.oauth2.prompt;

import com.odeyalo.kyrie.core.sso.RememberedLoggedUserAccountsContainer;
import com.odeyalo.kyrie.support.html.DefaultTemplateResolver;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * login - The Authorization Server SHOULD prompt the End-User for re-authentication. If it cannot reauthenticate the End-User, it MUST return an error, typically login_required.
 * </p>
 */
public class LoginPromptHandler implements PromptHandler {
    private final TemplateResolver templateResolver;

    public LoginPromptHandler(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    /**
     * Handle the 'login' prompt that defined in OpenID Specification.
     * Always return login page to re-authenticate the user, even if request contains session with user accounts.
     *
     * @param model - model for this prompt, in most cases model is empty
     * @param request - current request to get info about user
     * @param response - response to return to user
     * @return - ModelAndView for LOGIN_TEMPLATE
     */
    @Override
    public ModelAndView handlePrompt(Model model, HttpServletRequest request, HttpServletResponse response) {
        return templateResolver.getTemplate(DefaultTemplateResolver.LOGIN_TEMPLATE_TYPE);
    }

    /**
     * Always return login template
     * @param model - model for this prompt
     * @param container - container with user accounts to avoid unnecessary calls to remember-me services
     * @param request - current request
     * @param response - response associated with this requeest
     * @return
     */
    @Override
    public ModelAndView handlePrompt(Model model, RememberedLoggedUserAccountsContainer container, HttpServletRequest request, HttpServletResponse response) {
        return templateResolver.getTemplate(DefaultTemplateResolver.LOGIN_TEMPLATE_TYPE);
    }

    /**
     * Return {@link PromptType#LOGIN}
     * @return - always PromptType#LOGIN
     */
    @Override
    public PromptType getType() {
        return PromptType.LOGIN;
    }
}
