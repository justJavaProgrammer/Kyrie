package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.kyrie.controllers.support.FormProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto class to represent the required fields to get access token using OAUTH2.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAccessTokenRequestDTO {
    @JsonProperty("grant_type")
    @FormProperty("grant_type")
    private String grantType;
    private String code;
    @JsonProperty("redirect_uri")
    @FormProperty("redirect_uri")
    private String redirectUrl;
    @JsonProperty("client_id")
    @FormProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    @FormProperty("client_secret")
    private String clientSecret;
}
