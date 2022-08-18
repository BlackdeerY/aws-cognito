package io.blackdeer.cognito.user;

//import io.blackdeer.cognito.aws.CognitoAuthentication;

import io.blackdeer.cognito.aws.CognitoJWTParser;
import io.blackdeer.cognito.aws.CognitoService;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("/info")
    public ResponseEntity getInfo(@RequestHeader(value = "Authorization") String authorization) {
        String claimsOrNull = CognitoJWTParser.getCalimsOrNull(authorization);
        if (claimsOrNull == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity(claimsOrNull, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/db")
    public ResponseEntity getAccount(@RequestHeader(value = "Authorization") String authorization) {
        String subOrNull = CognitoJWTParser.getSubOrNull(authorization);
        if (subOrNull == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Account accountOrNull = accountService.getAccountOrNull(subOrNull);
        if (accountOrNull == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(accountOrNull.toString(), HttpStatus.OK);
    }

    @PutMapping("/insert")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public ResponseEntity insert(@RequestHeader(value = "Authorization") String authorization) {
        JSONObject userinfoOrNull = CognitoService.getUserInfoOrNull(authorization);
        if (userinfoOrNull == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        boolean isInserted = accountService.insertAccount(userinfoOrNull);
        if (isInserted == false) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity(userinfoOrNull.toString(), httpHeaders, HttpStatus.OK);
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
}
