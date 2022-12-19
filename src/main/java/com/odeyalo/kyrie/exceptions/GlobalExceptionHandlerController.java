package com.odeyalo.kyrie.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerController {
    private final static String MISSED_PARAMETER_NAME_KEY = "missedParameter";
    private final static String DESCRIPTION_KEY = "description";

    /**
     * Handle the MissingServletRequestParameterException. This is helpful to return the description about error
     * @param exception - MissingServletRequestParameterException that was occurred
     * @return - ResponseEntity with description about what request parameter was missed
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException exception) {
        Map<String, Object> body = getDefaultBody(HttpStatus.BAD_REQUEST);
        body.put(DESCRIPTION_KEY, "Missed the required parameter: " + exception.getParameterName());
        body.put(MISSED_PARAMETER_NAME_KEY, exception.getParameterName());
        return ResponseEntity.badRequest().body(body);
    }


    /**
     * Return the default response body with
     * timestamp,
     * status(Integer http status representation),
     * error(String http status representation),
     * path of current request
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
