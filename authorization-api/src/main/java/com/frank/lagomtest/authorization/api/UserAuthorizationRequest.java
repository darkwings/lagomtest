package com.frank.lagomtest.authorization.api;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author ftorriani
 */
public class UserAuthorizationRequest {

    private final String token;

    @JsonCreator
    private UserAuthorizationRequest( String token ) {
        this.token = token;
    }

    public static UserAuthorizationRequest from( String token ) {
        return new UserAuthorizationRequest( token );
    }

    public String getToken() {
        return token;
    }
}
