package com.odeyalo.kyrie.core.oauth2.prompt;

import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.sso.RememberedLoggedUserAccountsContainer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link PromptHandler} implementation that used to handle 'combined' prompt.
 * <p>
 *     Combined prompt is custom prompt defined by Kyrie Server, that used to login user by different conditions
 *     <ul>
 *         <li>If user is logged and remember me container contains only one account, then 'consent' prompt will be used</li>
 *         <li>If user is logged and remember me container contains more than one account, then 'select_account' will be used</li>
 *         <li>If user is not logged, then 'login' prompt will be used</li>
 *     </ul>
 * </p>
 */
@Component
public class CombinedPromptHandler implements PromptHandler {
    public static final String COMBINED_PROMPT_TYPE_NAME = "combined";
    private final RememberMeService rememberMeService;
    private final PromptHandlerFactory factory;

    public CombinedPromptHandler(RememberMeService rememberMeService, @Lazy PromptHandlerFactory factory) {
        this.rememberMeService = rememberMeService;
        this.factory = factory;
    }

    @Override
    public ModelAndView handlePrompt(Model model, HttpServletRequest request, HttpServletResponse response) {
        RememberedLoggedUserAccountsContainer container = rememberMeService.autoLogin(request);
        return handlePrompt(model, container, request, response);
    }

    @Override
    public ModelAndView handlePrompt(Model model, RememberedLoggedUserAccountsContainer container, HttpServletRequest request, HttpServletResponse response) {
        if (container.size() == 1) {
            return factory.getHandler(PromptType.CONSENT).handlePrompt(model, container, request, response);
        }
        if (container.size() > 1) {
            return factory.getHandler(PromptType.SELECT_ACCOUNT).handlePrompt(model, container, request, response);
        }
        // Return LOGIN prompt by default
        return factory.getHandler(PromptType.LOGIN).handlePrompt(model, container, request, response);
    }

    @Override
    public PromptType getType() {
        return new PromptType(COMBINED_PROMPT_TYPE_NAME);
    }
}
