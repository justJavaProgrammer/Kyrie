package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represent generic info about error to return it to client
 */
@Data
@NoArgsConstructor
public class ApiErrorMessage {
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;

    public ApiErrorMessage(String errorName, String errorDescription) {
        this.error = errorName;
        this.errorDescription = errorDescription;
    }
}
