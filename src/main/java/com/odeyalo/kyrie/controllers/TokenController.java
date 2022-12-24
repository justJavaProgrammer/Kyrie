package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.controllers.support.AdvancedModelAttribute;
import com.odeyalo.kyrie.core.oauth2.Oauth2TokenGeneratorFacade;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessTokenManager;
import com.odeyalo.kyrie.dto.AccessTokenIntrospectionResponse;
import com.odeyalo.kyrie.dto.GetAccessTokenRequestDTO;
import com.odeyalo.kyrie.dto.KyrieSuccessfulObtainTokenResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to obtain an access token and token info
 */
@RestController
@Log4j2
public class TokenController {
    private final Oauth2AccessTokenManager tokenManager;
    private Oauth2TokenGeneratorFacade generatorFacade;

    public TokenController(Oauth2AccessTokenManager tokenManager, Oauth2TokenGeneratorFacade generatorFacade) {
        this.tokenManager = tokenManager;
        this.generatorFacade = generatorFacade;
    }

    /**
     * Method to process /token endpoint that support only 'application/json' type
     *
     * @param body - body in json
     * @return - access token
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolveAccessTokenUsingJson(@RequestBody GetAccessTokenRequestDTO body) {
        return getStringResponseEntity(body);
    }

    @PostMapping(value = "/token", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> resolveAccessTokenUsingFormData(@AdvancedModelAttribute GetAccessTokenRequestDTO body) {
        return getStringResponseEntity(body);
    }

    @PostMapping(value = "/tokeninfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> tokenInfoRfc7662(@RequestParam String token) {
        log.info("Received token: {}", token);
        Oauth2AccessToken info = tokenManager.getTokenInfo(token);
        AccessTokenIntrospectionResponse response = getAccessTokenIntrospectionResponse(info);
        log.info("Body: {}", response);
        return ResponseEntity.ok(response);
    }

    private AccessTokenIntrospectionResponse getAccessTokenIntrospectionResponse(Oauth2AccessToken info) {
        if (info.isExpired()) {
            return AccessTokenIntrospectionResponse.nonActive();
        }
        return AccessTokenIntrospectionResponse.builder(true)
                .expiresIn(info.getExpiresIn().getEpochSecond())
                .scope(info.getScope()).build();
    }

    private ResponseEntity<KyrieSuccessfulObtainTokenResponse> getStringResponseEntity(GetAccessTokenRequestDTO body) {
        log.info("body {}", body);
        String code = body.getCode();
        Oauth2AccessToken token = tokenManager.obtainAccessTokenByAuthorizationCode(Oauth2ClientCredentials.of(body.getClientId(), body.getClientSecret()), code);
        return ResponseEntity.ok(
                new KyrieSuccessfulObtainTokenResponse(token.getTokenValue(),
                        token.getTokenType().getValue(),
                        token.getExpiresIn().getEpochSecond(),
                        token.getScope())
        );
    }
}

