package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.User;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.ValidationResult;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeManager;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenMetadata;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProvider;
import com.odeyalo.kyrie.dto.TokensResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AccessTokenReturnerImpl implements AccessTokenReturner {
    private final ClientCredentialsValidator clientCredentialsValidator;
    private final AuthorizationCodeManager authorizationCodeManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AccessTokenReturnerImpl(ClientCredentialsValidator clientCredentialsValidator, AuthorizationCodeManager authorizationCodeManager, JwtTokenProvider tokenProvider) {
        this.clientCredentialsValidator = clientCredentialsValidator;
        this.authorizationCodeManager = authorizationCodeManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public TokensResponse getToken(String clientId, String clientSecret, String authorizationCode) throws ObtainTokenException {
        ValidationResult validationResult = clientCredentialsValidator.validateCredentials(clientId, clientSecret);
        if (!validationResult.isSuccess()) {
            throw new ObtainTokenException("Client credentials is wrong and cannot be used to obtain the access token");
        }
        AuthorizationCode authCode = authorizationCodeManager.getAuthorizationCodeByAuthorizationCodeValue(authorizationCode);
        if (authCode == null || authCode.isExpired()) {
            throw new ObtainTokenException("The authorization code does not found or expired");
        }
        JwtTokenMetadata metadata = tokenProvider.generateJwtToken(new User("odeyalo", "password", Set.of("USER")), new String[]{"read"});
        return TokensResponse.builder().token(metadata.getToken()).expiresIn(metadata.getExpiresIn()).prefix(metadata.getPrefix()).scopes(metadata.getScopes()).build();
    }
}
