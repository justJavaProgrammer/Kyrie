package com.odeyalo.kyrie;

import com.odeyalo.kyrie.config.annotation.EnableKyrieAuthorizationServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Used to cache Spring application context and avoid context recreating to boost performance
 */
@SpringBootTest
@EnableKyrieAuthorizationServer
@TestPropertySource(locations = "classpath:application-test.properties")
public class AbstractIntegrationTest {
}
