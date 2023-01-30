package com.odeyalo.kyrie.core.oauth2.prompt;

import com.odeyalo.kyrie.core.sso.RememberedLoggedUserAccountsContainer;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PromptHandler {

    /**
     * Handle the given prompt defined in OpenID specification.
     * @param model - model for this prompt, in most cases model is empty
     * @param request - current request to get info about user
     * @param response - response to return to user
     */
    ModelAndView handlePrompt(Model model, HttpServletRequest request, HttpServletResponse response);

    /**
     * Handle the given prompt defined in OpenID specification.
     * @param model - model for this prompt
     * @param container - container with user accounts to avoid unnecessary calls to remember-me services
     * @param request - current request
     * @param response - response associated with this requeest
     * @return - ModelAndView to return to user or null
     */
    ModelAndView handlePrompt(Model model, RememberedLoggedUserAccountsContainer container, HttpServletRequest request, HttpServletResponse response);

    /**
     * Prompt that implementation supports.
     * @return - supported prompt type, never null
     */
    PromptType getType();
}
