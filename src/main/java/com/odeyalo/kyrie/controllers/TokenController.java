package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.controllers.support.AdvancedModelAttribute;
import com.odeyalo.kyrie.core.oauth2.tokens.AccessTokenReturner;
import com.odeyalo.kyrie.dto.GetAccessTokenRequestDTO;
import com.odeyalo.kyrie.dto.TokensResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to obtain an access token
 */
@RestController
@Log4j2
public class TokenController {
    private final AccessTokenReturner accessTokenReturner;

    public TokenController(AccessTokenReturner accessTokenReturner) {
        this.accessTokenReturner = accessTokenReturner;
    }

    /**
     * Method to process /token annotation that support only 'application/json' type
     *
     * @param body - body in json
     * @return - access token
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authorization(@RequestBody GetAccessTokenRequestDTO body) {
        return getStringResponseEntity(body);
    }

    @PostMapping(value = "/token", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> authorizationFormData(@AdvancedModelAttribute GetAccessTokenRequestDTO body) {
        return getStringResponseEntity(body);
    }

    private ResponseEntity<TokensResponse> getStringResponseEntity(GetAccessTokenRequestDTO body) {
        log.info("body {}", body);
        String code = body.getCode();
        return ResponseEntity.ok(accessTokenReturner.getToken(body.getClientId(), body.getClientSecret(), code));
    }
}
