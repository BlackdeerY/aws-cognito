package io.blackdeer.cognito.aws;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class CognitoJWTParser {

    private static final Logger logger = LoggerFactory.getLogger(CognitoJWTParser.class);

    private static JWTClaimsSet parseAndGetClaimsOrNull(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        JWTClaimsSet jwtClaimsSet = null;
        try {
            jwtClaimsSet = JWTParser.parse(accessToken).getJWTClaimsSet();
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return jwtClaimsSet;
    }

    public static String getCalimsOrNull(String accessToken) {
        JWTClaimsSet claimsSetOrNull = parseAndGetClaimsOrNull(accessToken);
        if (claimsSetOrNull == null) {
            return null;
        }
        return claimsSetOrNull.toString();
    }

    public static String getSubOrNull(String accessToken) {
        JWTClaimsSet claimsSetOrNull = parseAndGetClaimsOrNull(accessToken);
        if (claimsSetOrNull == null) {
            return null;
        }
        return claimsSetOrNull.getSubject();
    }

    public static String getUsernameOrNull(String accessToken) {
        JWTClaimsSet claimsSetOrNull = parseAndGetClaimsOrNull(accessToken);
        if (claimsSetOrNull == null) {
            return null;
        }
        String username = null;
        try {
            username = claimsSetOrNull.getStringClaim("username");
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return username;
    }
}
