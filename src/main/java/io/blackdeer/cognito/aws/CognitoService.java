package io.blackdeer.cognito.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CognitoService {

    private static final Logger logger = LoggerFactory.getLogger(CognitoService.class);

    @Value("${aws.cognito.user-pool-id}")
    private String userPoolId;

    @Value("${aws.cognito.client-id}")
    private String clientId;

    @Value("${aws.cognito.client-secret}")
    private String clientSecret;

    private final AWSCredentials awsCredentials;
    private final AWSCredentialsProvider awsCredentialsProvider;
    private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;

    @Autowired
    public CognitoService(@Value("${aws.region}") String awsRegion,
                          @Value("${aws.access-key-id}") String awsAccessKeyId,
                          @Value("${aws.secret-key}") String awsSecretKey) {
        this.awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return awsAccessKeyId;
            }

            @Override
            public String getAWSSecretKey() {
                return awsSecretKey;
            }
        };
        this.awsCredentialsProvider = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return awsCredentials;
            }

            @Override
            public void refresh() {
            }
        };
        AWSCognitoIdentityProviderClientBuilder awsCognitoIdentityProviderClientBuilder = AWSCognitoIdentityProviderClient.builder();
        awsCognitoIdentityProviderClientBuilder.setRegion(awsRegion);
        awsCognitoIdentityProviderClientBuilder.setCredentials(awsCredentialsProvider);
        awsCognitoIdentityProvider = awsCognitoIdentityProviderClientBuilder.build();
    }

    public CognitoUser getUserOrNullByPersonalAccessToken(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        GetUserRequest getUserRequest = new GetUserRequest();
        getUserRequest.setAccessToken(accessToken);
        GetUserResult getUserResult = null;
        try {
            getUserResult = awsCognitoIdentityProvider.getUser(getUserRequest);
        } catch (ResourceNotFoundException e) {
            // This exception is thrown when the Amazon Cognito service can't find the requested resource.
            logger.error(e.getMessage(), e);
        } catch (InvalidParameterException e) {
            // This exception is thrown when the Amazon Cognito service encounters an invalid parameter.
            logger.error(e.getMessage(), e);
        } catch (NotAuthorizedException e) {
            // This exception is thrown when a user isn't authorized.
            logger.info(e.getMessage(), e);
        } catch (TooManyRequestsException e) {
            // This exception is thrown when the user has made too many requests for a given operation.
            logger.error(e.getMessage(), e);
        } catch (PasswordResetRequiredException e) {
            // This exception is thrown when a password reset is required.
            logger.error(e.getMessage(), e);
        } catch (UserNotFoundException e) {
            // This exception is thrown when a user isn't found.
            logger.error(e.getMessage(), e);
        } catch (UserNotConfirmedException e) {
            // This exception is thrown when a user isn't confirmed successfully.
            logger.error(e.getMessage(), e);
        } catch (InternalErrorException e) {
            // This exception is thrown when Amazon Cognito encounters an internal error.
            logger.error(e.getMessage(), e);
        } catch (ForbiddenException e) {
            // This exception is thrown when WAF doesn't allow your request based on a web ACL that's associated with your user pool.
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (getUserResult == null) {
            return null;
        } else {
            List<AttributeType> attributeTypeList = getUserResult.getUserAttributes();
            String sub = null;
            String email = null;
            String idpOrNull = null;
            String userIdOrNull = null;
            for (AttributeType attributeType : attributeTypeList) {
                String attributeName = attributeType.getName();
                if (attributeName.equals("sub")) {
                    sub = attributeType.getValue();
                } else if (attributeName.equals("email")) {
                    email = attributeType.getValue();
                } else if (attributeName.equals("identities")) {
                    JSONObject jsonObject = new JSONArray(attributeType.getValue()).getJSONObject(0);
                    if (jsonObject.has("providerName")) {
                        idpOrNull = jsonObject.getString("providerName");
                    }
                    if (jsonObject.has("userId")) {
                        userIdOrNull = jsonObject.getString("userId");
                    }
                }
            }
            return new CognitoUser(sub, getUserResult.getUsername(), email, idpOrNull, userIdOrNull);
        }
    }

    private List<UserType> getUsersListOrNull() {
        ListUsersRequest listUsersRequest = new ListUsersRequest();
        listUsersRequest.setUserPoolId(userPoolId);
        try {
            ListUsersResult listUsersResult = awsCognitoIdentityProvider.listUsers(listUsersRequest);
            return listUsersResult.getUsers();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public void printAllUsers() {
        List<UserType> userTypeList = getUsersListOrNull();
        if (userTypeList != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (UserType userType : userTypeList) {
                String username = userType.getUsername();
                List<AttributeType> attributeTypeList = userType.getAttributes();
                String sub = null;
                String email = null;
                String idpOrNull = null;
                String userIdOrNull = null;
                for (AttributeType attributeType : attributeTypeList) {
                    String attributeName = attributeType.getName();
                    if (attributeName.equals("sub")) {
                        sub = attributeType.getValue();
                    } else if (attributeName.equals("email")) {
                        email = attributeType.getValue();
                    } else if (attributeName.equals("identities")) {
                        JSONObject jsonObject = new JSONArray(attributeType.getValue()).getJSONObject(0);
                        if (jsonObject.has("providerName")) {
                            idpOrNull = jsonObject.getString("providerName");
                        }
                        if (jsonObject.has("userId")) {
                            userIdOrNull = jsonObject.getString("userId");
                        }
                    }
                }
                CognitoUser cognitoUser = new CognitoUser(sub, username, email, idpOrNull, userIdOrNull);
                stringBuilder.append(cognitoUser.toString());
            }
            System.out.println(stringBuilder.toString());
        }
    }

    public boolean signOutGlobal(String username) {
        AdminUserGlobalSignOutRequest adminUserGlobalSignOutRequest = new AdminUserGlobalSignOutRequest();
        adminUserGlobalSignOutRequest.setUsername(username);
        adminUserGlobalSignOutRequest.setUserPoolId(userPoolId);
        try {
            AdminUserGlobalSignOutResult adminUserGlobalSignOutResult = awsCognitoIdentityProvider.adminUserGlobalSignOut(adminUserGlobalSignOutRequest);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public void signOutAllGlobal() {
        List<UserType> userTypeList = getUsersListOrNull();
        if (userTypeList != null) {
            for (UserType userType : userTypeList) {
                signOutGlobal(userType.getUsername());
            }
        }
    }
}
