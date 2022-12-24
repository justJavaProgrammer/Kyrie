package com.odeyalo.kyrie.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
public class UserController {
    //todo implement the logic
    @GetMapping("/user/info")
    public ResponseEntity<?> getInfo(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> body = new HashMap<>();
        body.put("username", "aboba");
        body.put("email", "thedolbore.com");
        body.put("picture", "https://i.pinimg.com/564x/c1/17/51/c11751da58cae417c8297eb238be63f2.jpg");
        log.info("Body: {}", body);
        return ResponseEntity.ok(body);
    }
}
