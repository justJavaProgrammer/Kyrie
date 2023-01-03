package com.odeyalo.kyrie;

import com.odeyalo.kyrie.config.annotation.EnableKyrieAuthorizationServer;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Used to cache Spring application context and avoid context recreating to boost performance
 */
@SpringBootTest
@EnableKyrieAuthorizationServer
public class AbstractIntegrationTest {
}
