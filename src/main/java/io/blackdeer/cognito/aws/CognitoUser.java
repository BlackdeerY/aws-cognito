package io.blackdeer.cognito.aws;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CognitoUser {
    private final String sub;
    private final String username;
    private final String email;
    private final String idp;
    private final String userId;

    public CognitoUser(@NonNull String sub,
                       @NonNull String username,
                       @NonNull String email,
                       @Nullable String idpOrNull,
                       @Nullable String userIdOrNull) {
        assert (sub != null);
        assert (username != null);
        assert (email != null);
        this.sub = sub;
        this.username = username;
        this.email = email;
        this.idp = idpOrNull;
        this.userId = userIdOrNull;
    }

    public String getSub() {
        return sub;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getIdp() {
        return idp;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return String.format("{\"sub\": \"%s\", \"username\": \"%s\", \"email\": \"%s\", \"idp\": \"%s\", \"userId\": \"%s\"}", sub, username, email, idp, userId);
    }
}
