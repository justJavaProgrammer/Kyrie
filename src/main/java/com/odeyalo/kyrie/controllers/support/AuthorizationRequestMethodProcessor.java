package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.controllers.support.validation.ValidAuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.support.grant.AuthorizationGrantTypeResolver;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;
import com.odeyalo.kyrie.exceptions.RedirectUriAwareOauth2Exception;
import com.odeyalo.kyrie.support.AdvancedStringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * <p> {@link HandlerMethodArgumentResolver} implementation used to resolve the {@link AuthorizationRequest} from request.</p>
 * <p> It will be invoked only for method parameters that is AuthorizationRequest type</p>
 * <p>
 * AuthorizationRequestMethodProcessor supports {@link AuthorizationRequest} validation.
 * To enable the validation for AuthorizationRequest method parameter MUST be annotated with the {@link ValidAuthorizationRequest} annotation
 * </p>
 */
public class AuthorizationRequestMethodProcessor implements HandlerMethodArgumentResolver {
    private final String2ResponseTypeConverter responseTypeConverter;
    private final SpaceSeparatedStringToArrayConverter arrayConverter;
    private final AuthorizationGrantTypeResolver grantTypeResolver;
    private final AuthorizationRequestValidator authorizationRequestValidator;

    public AuthorizationRequestMethodProcessor(String2ResponseTypeConverter responseTypeConverter, SpaceSeparatedStringToArrayConverter arrayConverter,
                                               AuthorizationGrantTypeResolver grantTypeResolver,
                                               AuthorizationRequestValidator authorizationRequestValidator) {
        this.responseTypeConverter = responseTypeConverter;
        this.arrayConverter = arrayConverter;
        this.grantTypeResolver = grantTypeResolver;
        this.authorizationRequestValidator = authorizationRequestValidator;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameter().getType().equals(AuthorizationRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) webRequest.getNativeRequest();

        AuthorizationRequest authorizationRequest = buildAuthorizationRequest(httpRequest);

        validateIfNecessary(parameter, authorizationRequest);

        return authorizationRequest;
    }

    /**
     * Build the AuthorizationRequest based on request parameters
     *
     * @param request - current request
     * @return - built AuthorizationRequest
     * @throws MissingServletRequestParameterException - if required request param is not presented
     */
    private AuthorizationRequest buildAuthorizationRequest(HttpServletRequest request) throws MissingServletRequestParameterException {
        String clientId = request.getParameter(Oauth2Constants.CLIENT_ID);
        String responseTypeParameter = request.getParameter(Oauth2Constants.RESPONSE_TYPE);
        String scopeParam = request.getParameter(Oauth2Constants.SCOPE);
        String redirectUri = request.getParameter(Oauth2Constants.REDIRECT_URI);
        String state = request.getParameter(Oauth2Constants.STATE);
        // Assert that the required parameters that must be presented in request is present and non null.
        assertNotNull(clientId, Oauth2Constants.CLIENT_ID);
        assertNotNull(responseTypeParameter, Oauth2Constants.RESPONSE_TYPE);
        assertNotNull(scopeParam, Oauth2Constants.SCOPE);
        assertNotNull(redirectUri, Oauth2Constants.REDIRECT_URI);

        Oauth2ResponseType[] responseTypes = getOauth2ResponseTypes(responseTypeParameter);

        String[] scopes = (String[]) arrayConverter.convert(scopeParam, TypeDescriptor.forObject(scopeParam), TypeDescriptor.array(TypeDescriptor.valueOf(String.class)));

        AuthorizationGrantType grantType = grantTypeResolver.resolveGrantType(responseTypes);


        return AuthorizationRequest.builder()
                .clientId(clientId)
                .responseTypes(responseTypes)
                .grantType(grantType)
                .redirectUrl(redirectUri)
                .scopes(scopes)
                .state(state)
                .build();
    }

    private Oauth2ResponseType[] getOauth2ResponseTypes(String responseTypeParameter) {
        Oauth2ResponseType[] responseTypes =
                Arrays.stream(AdvancedStringUtils.spaceDelimitedListToStringArray(responseTypeParameter))
                        .map(rawResponseType -> responseTypeConverter.convert(rawResponseType))
                        .toArray(Oauth2ResponseType[]::new);
        return responseTypes;
    }

    private void assertNotNull(String value, String parameterName) throws MissingServletRequestParameterException {
        if (value == null) {
            throw new MissingServletRequestParameterException(parameterName, String.class.getSimpleName());
        }
    }

    /**
     * Validate the AuthorizationRequest only if method parameter has {@link ValidAuthorizationRequest} annotation
     *
     * @param parameter            - method parameter that contains info about parameter
     * @param authorizationRequest - request to validate
     */
    private void validateIfNecessary(MethodParameter parameter, AuthorizationRequest authorizationRequest) {
        if (!parameter.hasParameterAnnotation(ValidAuthorizationRequest.class)) {
            return;
        }
        Oauth2ValidationResult validationResult = authorizationRequestValidator.validateAuthorizationRequest(authorizationRequest);
        if (!validationResult.isSuccess()) {
            throw resolveException(authorizationRequest.getRedirectUrl(), validationResult);
        }
    }

    private RuntimeException resolveException(String redirectUrl, Oauth2ValidationResult validationResult) {
        Oauth2ErrorType errorType = validationResult.getErrorType();

        if (Oauth2ErrorType.INVALID_REDIRECT_URI.equals(errorType) || Oauth2ErrorType.INVALID_CLIENT.equals(errorType)) {
            return new Oauth2Exception(
                    String.format("Failed to initialize Authorization request. Reason: %s", validationResult.getMessage()),
                    validationResult.getMessage(), errorType);
        }
        return new RedirectUriAwareOauth2Exception(String.format("Failed to initialize Authorization request. Reason: %s", validationResult.getMessage()),
                validationResult.getMessage(), redirectUrl, errorType);
    }
}
