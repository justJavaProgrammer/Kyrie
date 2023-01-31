package com.odeyalo.kyrie.core.oauth2.support.consent;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.flow.support.RedirectableOauth2FlowHandlerFacade;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.support.html.DefaultTemplateResolver;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * ConsentPageHandler that used to handle ONLY html form in consent page.
 */
@Component
public class DefaultKyrieHtmlFormConsentPageHandler implements ConsentPageHandler {
    public static final String AUTHENTICATED_USER_SESSION_ATTRIBUTE_NAME = "authenticated_user";
    @Autowired
    private TemplateResolver templateResolver;
    @Autowired
    private RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade;

    public static final String APPROVED_FORM_PARAMETER = "approved";
    public static final String DENIED_FORM_PARAMETER = "denied";

    /**
     * Store the current user in the session and return the consent page that was resolved by {@link TemplateResolver}
     * @param user - user that already authenticated but requires consent
     * @param authorizationRequest - authorization request associated with this consent page
     * @param request - current http request
     * @return - consent page
     */
    @Override
    public ModelAndView getConsentPage(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Assert.state(session != null, "The session must be presented for the request");

        session.setAttribute(AUTHENTICATED_USER_SESSION_ATTRIBUTE_NAME, user);

        ExtendedModelMap model = new ExtendedModelMap();

        model.addAttribute("user", user);
        model.addAttribute("scopes", authorizationRequest.getScopes());
        return templateResolver.getTemplate(DefaultTemplateResolver.CONSENT_VIEW_TEMPLATE_TYPE, model);
    }

    @Override
    public void handleSubmit(AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        String approved = request.getParameter(APPROVED_FORM_PARAMETER);
        Oauth2User user = (Oauth2User) request.getSession().getAttribute("user");

        if (approved != null) {
            // User approved access
            String redirectUri = redirectableOauth2FlowHandlerFacade.handleFlow(user, authorizationRequest);
            sendRedirect(response, redirectUri);
            return;
        }
        // User denied the access
        String redirectUri = UriComponentsBuilder.fromUriString(authorizationRequest.getRedirectUrl()).queryParam(Oauth2Constants.ERROR_PARAMETER_NAME, "access_denied").toUriString();

        sendRedirect(response, redirectUri);
    }

    @Override
    public void onAccessApproved(AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void onAccessDenied(AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {

    }


    private void sendRedirect(HttpServletResponse response, String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
