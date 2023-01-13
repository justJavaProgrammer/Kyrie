package com.odeyalo.kyrie;

import com.odeyalo.kyrie.config.annotation.EnableKyrieAuthorizationServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKyrieAuthorizationServer
public class KyrieApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyrieApplication.class, args);
    }
}
