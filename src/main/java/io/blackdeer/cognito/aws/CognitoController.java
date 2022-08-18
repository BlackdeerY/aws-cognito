package io.blackdeer.cognito.aws;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * ResourceServer와는 상관없는 부분입니다.
 * Cognito의 Auth Flow 중 code를 받아 access_token을 발급하는 부분입니다.
 * 현재는 그냥 모든 토큰을 출력해줍니다.
 */
@RestController
@RequestMapping("/cognito")
public class CognitoController {

    @Value("${spring.security.oauth2.client.provider.cognito.token-uri}")
    private String cognitoTokenUri;

    @Value("${spring.security.oauth2.client.registration.cognito.redirect-uri}")
    private String cognitoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
    private String cognitoClientId;

    @Value("${spring.security.oauth2.client.registration.cognito.client-secret}")
    private String cognitoClientSecret;

    public CognitoController() {
    }

    @GetMapping("/token")
    public ResponseEntity codeToToken(@RequestParam(value = "code", required = false) String code) {
        if (code == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.add("Authorization",
                String.format("Basic %s",
                        Base64.getEncoder().encodeToString(
                                String.format("%s:%s",
                                        cognitoClientId,
                                        cognitoClientSecret).
                                        getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
        String requestBody = String.format("grant_type=%s&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
                "authorization_code",
                cognitoClientId,
                cognitoClientSecret,
                code,
                cognitoRedirectUri);
        HttpEntity<String> httpRequest = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<String> tokenResponse = new RestTemplate().exchange(cognitoTokenUri, HttpMethod.POST, httpRequest, String.class);
        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity(tokenResponse.getStatusCode());
        }
        JSONObject tokenResult = null;
        try {
            tokenResult = new JSONObject(tokenResponse.getBody());
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
//        String accessToken = tokenResult.getString("access_token");
//        JSONObject getUserInfoOrNull = CognitoService.getUserInfoOrNull(accessToken);
//        if (getUserInfoOrNull == null) {
//            getUserInfoOrNull = tokenResult;
//        } else {
//            getUserInfoOrNull.append("tokenResult", tokenResult);
//        }
//        System.out.println(getUserInfoOrNull.toString(4));
        httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity(tokenResult.toString(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(@RequestHeader(required = false) HttpHeaders httpHeaders,
                                 @RequestParam(required = false) Map<String, String> requestParams,
                                 @RequestBody(required = false) String body) {
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
        System.out.println(stringBuilder.toString());
        return new ResponseEntity(HttpStatus.OK);
    }
}
