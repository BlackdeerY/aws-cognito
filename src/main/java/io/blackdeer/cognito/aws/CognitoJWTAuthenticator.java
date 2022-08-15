/*
package io.blackdeer.cognito.aws;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CognitoJWTAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(CognitoJWTAuthenticator.class);

    private static final String keyForHeader = "Authorization";
    private static final String authorizationPrefix = "Bearer ";

    @Autowired
    public CognitoJWTAuthenticator() {
    }

    public String getSubOrNull(String accessToken) {
        JSONObject jwtPayload = null;
        try {
            jwtPayload = CognitoJWTParser.getPayload(accessToken);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        if (jwtPayload.has("iss") == false) {
            logger.error("jwt payload에 iss가 없음!");
            return null;
        }
        String iss = jwtPayload.getString("iss");
        if (iss.equals(issuer) == false) {
            logger.error("jwt iss 불일치!");
            return null;
        }
        if (jwtPayload.has("exp") == false) {
            logger.error("jwt payload에 exp가 없음!");
            return null;
        }
        long exp = jwtPayload.getLong("exp");
        if (System.currentTimeMillis() >= exp) {
            logger.info("만료된 accessToken");
            return null;
        }
        if (jwtPayload.has("sub") == false) {
            logger.error("jwt payload에 sub이 없음!");
            return null;
        }
        String sub = jwtPayload.getString("sub");
        return sub;
    }

    public Object getAuthenticationOrNull(HttpServletRequest request) {

        String authorizationInHeader = request.getHeader(keyForHeader);
        if (authorizationInHeader == null) {
            return null;
        }
        if (authorizationInHeader.startsWith(authorizationPrefix)) {
            authorizationInHeader = authorizationInHeader.substring(authorizationPrefix.length());
        }




        return null;
    }
}
 */
