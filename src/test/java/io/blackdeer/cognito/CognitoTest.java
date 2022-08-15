package io.blackdeer.cognito;

import io.blackdeer.cognito.aws.CognitoService;
import io.blackdeer.cognito.aws.CognitoUser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CognitoTest {

    private static final Logger logger = LoggerFactory.getLogger(CognitoTest.class);

    @Autowired
    private CognitoService cognitoService;

    @Test
    @Order(1)
    @DisplayName("모든 유저 조회")
    public void listCognitoUsers() {
//        CognitoUser cognitoUser = cognitoService.getUserOrNullByPersonalAccessToken("");
//        System.out.println(cognitoUser);
//        cognitoService.printAllUsers();
//        cognitoService.signOutAllGlobal();
    }
}
