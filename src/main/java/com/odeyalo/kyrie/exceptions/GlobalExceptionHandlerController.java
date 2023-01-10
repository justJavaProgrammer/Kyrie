package com.odeyalo.kyrie.exceptions;


import com.odeyalo.kyrie.dto.ApiErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerController {
    private final static String MISSED_PARAMETER_NAME_KEY = "missedParameter";
    private final static String DESCRIPTION_KEY = "description";
    public static final String ERROR_PARAMETER_NAME = "error";
    public static final String ERROR_DESCRIPTION_PARAMETER_NAME = "error_description";

    /**
     * Handle the MissingServletRequestParameterException. This is helpful to return the description about error
     *
     * @param exception - MissingServletRequestParameterException that was occurred
     * @return - ResponseEntity with description about what request parameter was missed
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException exception) {
        Map<String, Object> body = getDefaultBody(HttpStatus.BAD_REQUEST);
        body.put(DESCRIPTION_KEY, "Missed the required parameter: " + exception.getParameterName());
        body.put(MISSED_PARAMETER_NAME_KEY, exception.getParameterName());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(value = Oauth2Exception.class)
    public ResponseEntity<?> handleOauth2Exception(Oauth2Exception exception) {
        if (exception instanceof InvalidRedirectUriOauth2Exception || exception.getErrorType() == Oauth2ErrorType.INVALID_REDIRECT_URI) {
            ApiErrorMessage apiErrorMessage = new ApiErrorMessage(Oauth2ErrorType.INVALID_REDIRECT_URI.getErrorName(), "The request can't be processed since redirect_uri parameter is  not valid. Contact developer if error remains");
            return ResponseEntity.ok(apiErrorMessage);
        }
        ApiErrorMessage body = new ApiErrorMessage(exception.getErrorType().getErrorName(), exception.getDescription());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(body);
    }


    @ExceptionHandler(value = RedirectUriAwareOauth2Exception.class)
    public ResponseEntity<?> handleRedirectUriAwareOauth2Exception(RedirectUriAwareOauth2Exception exception) {
        String uri = exception.getRedirectUri();
        // If uri is not set, then delegate it to generic exception handler
        if (uri == null) {
            return handleOauth2Exception(exception);
        }
        String redirectUri = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam(ERROR_PARAMETER_NAME, exception.getErrorType().getErrorName())
                .queryParam(ERROR_DESCRIPTION_PARAMETER_NAME, exception.getDescription())
                .toUriString();
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUri).build();
    }


    /**
     * Return the default response body with
     * timestamp,
     * status(Integer http status representation),
     * error(String http status representation),
     * path of current request
     *
     * @param status - status of response
     * @return - map with default body
     */
    Map<String, Object> getDefaultBody(HttpStatus status) {
        HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HashMap<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Timestamp(System.currentTimeMillis()));
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("path", currentRequest.getRequestURI());
        return body;
    }
}
