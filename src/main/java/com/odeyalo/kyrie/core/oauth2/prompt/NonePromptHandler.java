package com.odeyalo.kyrie.core.oauth2.prompt;

import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.support.AuthorizationRequestContextHolder;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.sso.RememberedLoggedUserAccountsContainer;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * <p>
 *     The Authorization Server MUST NOT display any authentication or consent user interface pages.
 * </p>
 */
@Component
public class NonePromptHandler implements PromptHandler {
    private final RememberMeService rememberMeService;
    private final Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo endpointsInfo;
    public static final String INTERACTION_REQUIRED = "interaction_required";

    public NonePromptHandler(RememberMeService rememberMeService, Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo endpointsInfo) {
        this.rememberMeService = rememberMeService;
        this.endpointsInfo = endpointsInfo;
    }

    /**
     * Handle the NONE prompt defined in OpenID spec. Always send redirect to GET /login endpoint and never return ModelAndView
     * @param model - model for this prompt, in most cases model is empty
     * @param request - current request to get info about user
     * @param response - response to return to user
     * @return - always null
     */
    @Override
    public ModelAndView handlePrompt(Model model, HttpServletRequest request, HttpServletResponse response) {
        RememberedLoggedUserAccountsContainer container = rememberMeService.autoLogin(request);
        Map<String, Oauth2User> users = container.getUsersMap();
        // The user doesn't logged yet or have more than 1 account, send interaction_required by OpenID spec.
        if (CollectionUtils.isEmpty(users) || users.size() != 1) {
            sendInteractionRequiredRedirect(response);
        }
        // Get the login endpoint that was configured by Oauth2ServerEndpointsConfigurer
        String loginEndpointName = endpointsInfo.getLoginEndpointName();
        String id = users.keySet().stream().findFirst().get();
        String redirectUri = UriComponentsBuilder.fromPath(loginEndpointName).queryParam("user_id", id).toUriString();
        sendRedirect(response, redirectUri);
        return null;
    }

    private void sendInteractionRequiredRedirect(HttpServletResponse response) {
        AuthorizationRequest authorizationRequest = AuthorizationRequestContextHolder.getContext().getRequest();
        String redirectUri = UriComponentsBuilder.fromUriString(authorizationRequest.getRedirectUrl())
                .queryParam(Oauth2Constants.ERROR_PARAMETER_NAME, INTERACTION_REQUIRED)
                .toUriString();
        sendRedirect(response, redirectUri);
    }

    private void sendRedirect(HttpServletResponse response, String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException ex) {
            throw new IllegalArgumentException("The location is wrong");
        }
    }

    @Override
    public PromptType getType() {
        return PromptType.NONE;
    }
}
