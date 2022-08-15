package io.blackdeer.cognito.user;

import io.blackdeer.cognito.aws.CognitoUser;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/account")
public class AccountController {

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity handleException(ConstraintViolationException e) {
        logger.warn(e.getMessage(), e);
        return new ResponseEntity(HttpStatus.CONFLICT);
    }

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/test2")
    public ResponseEntity test2(@RequestAttribute(required = false) CognitoUser cognitoUser) {
        if (cognitoUser == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(cognitoUser.toString(), HttpStatus.OK);
    }

    @PutMapping("/insert")
    public ResponseEntity insert(@RequestAttribute(required = false) CognitoUser cognitoUser) {
        if (cognitoUser == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        boolean isInserted = accountService.insertAccount(cognitoUser);
        if (isInserted == false) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        return new ResponseEntity(cognitoUser.toString(), HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity test(@RequestHeader(required = false) HttpHeaders httpHeaders,
                               @RequestParam(required = false) Map<String, String> requestParams,
                               @RequestBody(required = false) String body,
                               HttpServletRequest httpServletRequest) {
        System.out.println(httpServletRequest);
        System.out.println(httpServletRequest.getRequestURI());
        System.out.println(httpServletRequest.getServletPath());
        System.out.println(httpServletRequest.getContextPath());
        System.out.println(httpServletRequest.getQueryString());
        StringBuilder stringBuilder = new StringBuilder();
        if (httpHeaders != null) {
            stringBuilder.append(String.format("<HttpHeaders> --------------------------------------------------%s", System.lineSeparator()));
            for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
                String key = entry.getKey();
                stringBuilder.append(String.format("\t[key: %s]%s", key, System.lineSeparator()));
                for (String value : entry.getValue()) {
                    stringBuilder.append(String.format("\t\t%s%s", value, System.lineSeparator()));
                }
            }
            stringBuilder.append(String.format("-------------------------------------------------- <HttpHeaders>%s", System.lineSeparator()));
        }
        if (requestParams != null) {
            stringBuilder.append(String.format("<Request Parameters> --------------------------------------------------%s", System.lineSeparator()));
            for (String key : requestParams.keySet()) {
                stringBuilder.append(String.format("\t[key: %s]%s", key, System.lineSeparator()));
                stringBuilder.append(String.format("\t\t%s%s", requestParams.get(key), System.lineSeparator()));
            }
            stringBuilder.append(String.format("-------------------------------------------------- <Request Parameters>%s", System.lineSeparator()));
        }
        if (body != null) {
            stringBuilder.append(String.format("<Request Body> --------------------------------------------------%s", System.lineSeparator()));
            stringBuilder.append(String.format("%s%s", body, System.lineSeparator()));
            stringBuilder.append(String.format("-------------------------------------------------- <Request Body>%s", System.lineSeparator()));
        }
        logger.info(stringBuilder.toString());
        System.out.println(stringBuilder.toString());
        return new ResponseEntity(HttpStatus.OK);
    }

//    @PostMapping
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public ResponseEntity login() {
//
//    }
}
