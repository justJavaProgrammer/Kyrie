package com.odeyalo.kyrie.core.oauth2.support.consent;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.flow.support.RedirectableOauth2FlowHandlerFacade;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.support.web.TemporaryRequestAttributesRepository;
import com.odeyalo.kyrie.support.html.DefaultTemplateResolver;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import io.jsonwebtoken.lang.Assert;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>ConsentPageHandler that used to handle ONLY html form in consent page.</p>
 * The implementation approves the consent only if the request contains {@link #approvedFormParameter} in parameters, otherwise the consent is denied.
 *
 * The {@link #approvedFormParameter} can be customized through {@link #setApprovedFormParameter(String)} method
 */
public class DefaultKyrieHtmlFormConsentPageHandler extends AbstractConsentPageHandler {
    private final RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade;
    private final TemporaryRequestAttributesRepository temporaryRequestAttributesRepository;
    private final TemplateResolver templateResolver;

    private String approvedFormParameter = "approved";


    public DefaultKyrieHtmlFormConsentPageHandler(RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade,
                                                  TemporaryRequestAttributesRepository temporaryRequestAttributesRepository,
                                                  TemplateResolver templateResolver) {
        this.redirectableOauth2FlowHandlerFacade = redirectableOauth2FlowHandlerFacade;
        this.temporaryRequestAttributesRepository = temporaryRequestAttributesRepository;
        this.templateResolver = templateResolver;
    }

    /**
     * Store the current user in the session and return the consent page that was resolved by {@link TemplateResolver}
     * @param user - user that already authenticated but requires consent
     * @param authorizationRequest - authorization request associated with this consent page
     * @param request - current http request
     * @return - consent page
     */
    @Override
    public ModelAndView getConsentPage(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        // Save the user only if the request does not contain AUTHENTICATED_USER_ATTRIBUTE_NAME attribute to avoid saving twice
        if (temporaryRequestAttributesRepository.get(request, AUTHENTICATED_USER_ATTRIBUTE_NAME) == null) {
            temporaryRequestAttributesRepository.save(request, AUTHENTICATED_USER_ATTRIBUTE_NAME, user);
        }

        ExtendedModelMap model = new ExtendedModelMap();

        model.addAttribute("user", user);
        model.addAttribute("scopes", authorizationRequest.getScopes());
        return templateResolver.getTemplate(DefaultTemplateResolver.CONSENT_VIEW_TEMPLATE_TYPE, model);
    }

    /**
     * The method implementation that checks if the {@link #approvedFormParameter} is presented in parameter, if so, then the consent is approved,
     * otherwise user rejected consent
     * @param authorizationRequest - AuthorizationRequest associated with current authentication session
     * @param request - current request
     * @return - {@link ConsentResult#approved(Oauth2User)} if the APPROVED_FORM_PARAMETER is presented in request parameters, {@link ConsentResult#denied(Oauth2User)})} otherwise
     */
    @Override
    ConsentResult isApproved(AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        String approved = request.getParameter(approvedFormParameter);

        Oauth2User user = temporaryRequestAttributesRepository.get(request, AUTHENTICATED_USER_ATTRIBUTE_NAME, Oauth2User.class);

        return approved != null ? ConsentResult.approved(user) : ConsentResult.denied(user);
    }

    @Override
    public void onAccessApproved(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        String redirectUri = redirectableOauth2FlowHandlerFacade.handleFlow(user, authorizationRequest);
        temporaryRequestAttributesRepository.remove(request, AUTHENTICATED_USER_ATTRIBUTE_NAME);
        sendRedirect(response, redirectUri);
    }

    @Override
    public void onAccessDenied(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        String redirectUri = UriComponentsBuilder.fromUriString(authorizationRequest.getRedirectUrl()).queryParam(Oauth2Constants.ERROR_PARAMETER_NAME, "access_denied").toUriString();
        temporaryRequestAttributesRepository.remove(request, AUTHENTICATED_USER_ATTRIBUTE_NAME);
        sendRedirect(response, redirectUri);
    }

    private void sendRedirect(HttpServletResponse response, String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String getApprovedFormParameter() {
        return approvedFormParameter;
    }

    public void setApprovedFormParameter(String approvedFormParameter) {
        Assert.notNull(approvedFormParameter, "Approved parameter must be not null!");
        this.approvedFormParameter = approvedFormParameter;
    }
}
