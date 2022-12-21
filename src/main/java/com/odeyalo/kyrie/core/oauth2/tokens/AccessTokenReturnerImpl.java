package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.support.ValidationResult;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeManager;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenReturnerImpl implements AccessTokenReturner {
    private final ClientCredentialsValidator clientCredentialsValidator;
    private final AuthorizationCodeManager authorizationCodeManager;
    private final Oauth2AccessTokenGenerator generator;

    @Autowired
    public AccessTokenReturnerImpl(ClientCredentialsValidator clientCredentialsValidator, AuthorizationCodeManager authorizationCodeManager, Oauth2AccessTokenGenerator generator) {
        this.clientCredentialsValidator = clientCredentialsValidator;
        this.authorizationCodeManager = authorizationCodeManager;
        this.generator = generator;
    }

    @Override
    public Oauth2AccessToken getToken(String clientId, String clientSecret, String authorizationCode) throws ObtainTokenException {
        ValidationResult validationResult = clientCredentialsValidator.validateCredentials(clientId, clientSecret);
        if (!validationResult.isSuccess()) {
            throw new ObtainTokenException("Client credentials is wrong and cannot be used to obtain an access token");
        }
        AuthorizationCode authCode = authorizationCodeManager.getAuthorizationCodeByAuthorizationCodeValue(authorizationCode);
        if (authCode == null || authCode.isExpired()) {
            throw new ObtainTokenException("The authorization code does not found or expired");
        }
        Oauth2User user = authCode.getUser();
        String[] scopes = authCode.getScopes();
        return generator.generateAccessToken(user, scopes);
    }
}
