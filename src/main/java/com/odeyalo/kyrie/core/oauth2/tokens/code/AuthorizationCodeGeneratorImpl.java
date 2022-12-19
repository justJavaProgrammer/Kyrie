package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Generate random authorization code with expire time
 */

@Component
public class AuthorizationCodeGeneratorImpl implements AuthorizationCodeGenerator {

    @Override
    public AuthorizationCode generateAuthorizationCode(Integer codeLength, Integer expireTimeSeconds, Oauth2User user, String[] scopes) {
        String code = RandomStringUtils.randomAlphabetic(codeLength);
        return AuthorizationCode.builder()
                .codeValue(code)
                .user(user)
                .expiresIn(LocalDateTime.now().plusSeconds(expireTimeSeconds))
                .scopes(scopes)
                .build();
    }
}
