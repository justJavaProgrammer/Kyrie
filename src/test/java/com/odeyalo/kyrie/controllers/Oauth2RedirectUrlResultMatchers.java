package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Factory with ResultMatcher utility methods that can be helpful while working with Oauth2/Oidc testing.
 *
 * @version 1.0
 * @see ResultMatcher
 */
public class Oauth2RedirectUrlResultMatchers {
    private static final Oauth2RedirectUrlResultMatchers INSTANCE = new Oauth2RedirectUrlResultMatchers();
    private final Logger logger = LoggerFactory.getLogger(Oauth2RedirectUrlResultMatchers.class);

    protected Oauth2RedirectUrlResultMatchers() {
    }

    public static Oauth2RedirectUrlResultMatchers oauth2() {
        return INSTANCE;
    }

    /**
     * Check if redirect url is containing access_token parameter
     *
     * @return - result matcher success or throw exception
     */
    public ResultMatcher isAccessTokenPresented() {
        return (result) -> {
            isParameterPresented(Oauth2Constants.ACCESS_TOKEN).match(result);
        };
    }

    /**
     * Check if redirect url is containing 'code' parameter
     *
     * @return - result matcher success or throw exception
     */
    public ResultMatcher isCodeParamPresented() {
        return (result) -> {
            isParameterPresented(Oauth2ResponseType.CODE.getSimplifiedName()).match(result);
        };
    }

    /**
     * Check if redirect url is containing token_type parameter
     *
     * @return - result matcher success or throw exception
     */
    public ResultMatcher isTokenTypeParamPresented() {
        return (result) -> {
            isParameterPresented(Oauth2Constants.TOKEN_TYPE).match(result);
        };
    }

    /**
     * Check if redirect url is containing expires_in parameter
     *
     * @return - result matcher success or throw exception
     */
    public ResultMatcher isExpiresInParamPresented() {
        return (result) -> {
            System.out.println("running");
            isParameterPresented(Oauth2Constants.EXPIRES_IN).match(result);
        };
    }

    /**
     * Check if redirect url is containing state parameter
     *
     * @return - result matcher success or throw exception
     */
    public ResultMatcher isStateParamPresented() {
        return (result) -> {
            isParameterPresented(Oauth2Constants.STATE).match(result);
        };
    }

    /**
     * Check if redirect url is containing id_token parameter
     *
     * @return - result matcher success or throw exception
     */
    public ResultMatcher isIdTokenParamPresented() {
        return (result) -> {
            isParameterPresented(OidcResponseType.ID_TOKEN.getSimplifiedName()).match(result);
        };
    }

    public ResultMatcher isParameterNotNull(String param) {
        return (result) -> {
            isParameterPresented(param).match(result);
            String redirectedUrl = result.getResponse().getRedirectedUrl();
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(redirectedUrl).build().getQueryParams();
            String actual = queryParams.getFirst(param);
            Assert.assertNotNull(actual);
        };
    }


    public ResultMatcher isParameterEqualTo(String param, String expected) {
        return (result) -> {
            isParameterPresented(param).match(result);
            String redirectedUrl = result.getResponse().getRedirectedUrl();
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(redirectedUrl).build().getQueryParams();
            String actual = queryParams.getFirst(param);
            Assert.assertEquals(expected, actual);
        };
    }

    public ResultMatcher isParameterPresented(String requiredParameter) {
        return (result) -> {
            logger.debug("Checking is parameter presented for: {}", requiredParameter);
            String redirectedUrl = result.getResponse().getRedirectedUrl();
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(redirectedUrl).build().getQueryParams();
            MatcherAssert.assertThat(String.format("The parameter with name %s is not presented", requiredParameter), queryParams.getFirst(requiredParameter) != null);
        };
    }
}
