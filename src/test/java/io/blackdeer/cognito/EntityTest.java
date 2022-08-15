package io.blackdeer.cognito;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(value = false)
public class EntityTest {

    private static final Logger logger = LoggerFactory.getLogger(EntityTest.class);

    @BeforeAll
    public static void beforeAll() {
    }

    @AfterAll
    public static void afterAll() {
    }

    @BeforeEach
    @Transactional
    public void beforeEach() {
    }

    @AfterEach
    @Transactional
    public void afterEach() {
    }

    @Test
    @Order(1)
    @DisplayName("테이블 생성")
    public void createTables() {

    }

}
