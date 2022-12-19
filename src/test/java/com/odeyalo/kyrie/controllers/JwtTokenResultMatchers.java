package com.odeyalo.kyrie.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

/**
 * Factory that contains utility methods with ResultMatchers that working with Jwt Token
 */
public class JwtTokenResultMatchers {

    public static JwtTokenResultMatchers jwt() {
        return new JwtTokenResultMatchers();
    }

    public ResultMatcher isTokenValid(String token) {
        return (result) -> {
            String unsignedToken = getUnsignedToken(token);
            Jwts.parser().parseClaimsJwt(unsignedToken);
        };
    }


    public ResultMatcher isClaimPresented(String token, String claimName) {
        return (result) -> {
            String unsignedToken = getUnsignedToken(token);
            Claims body = Jwts.parser().parseClaimsJwt(unsignedToken).getBody();
            Object o = body.get(claimName);
            MatcherAssert.assertThat(String.format("The claim: is not presented", claimName), o != null);
        };
    }

    public ResultMatcher isClaimTypeCorrect(String token, String claimName, Class<?> expected) {
        return (result) -> {
            String unsignedToken = getUnsignedToken(token);
            Claims body = Jwts.parser().parseClaimsJwt(unsignedToken).getBody();
            Object o = body.get(claimName);
            Assertions.assertEquals(expected, o.getClass());
        };
    }


    public ResultMatcher isClaimEqualTo(String token, String claimName, Object expected) {
        return (result) -> {
            String unsignedToken = getUnsignedToken(token);
            Claims body = Jwts.parser().parseClaimsJwt(unsignedToken).getBody();
            Object o = body.get(claimName);
            Assertions.assertEquals(expected, o);
        };
    }



    public ResultMatcher isClaimArrayContains(String token, String claimName, Object[] expected) {
        return (result) -> {
            String unsignedToken = getUnsignedToken(token);
            Claims body = Jwts.parser().parseClaimsJwt(unsignedToken).getBody();
            Object o = body.get(claimName);
            if (!(o instanceof List)) {
                throw new AssertionError(String.format("Failed to compare since claim with name: %s is not an array", claimName));
            }
            List<?> actualArray = (List<?>) o;
            Assertions.assertEquals(List.of(expected), actualArray);
        };
    }


    protected String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        return splitToken[0] + "." + splitToken[1] + ".";
    }
}
