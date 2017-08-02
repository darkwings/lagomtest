package com.frank.lagomtest.authorization.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author ftorriani
 */
public class UserAuthorization {

    private final String username;
    private final Set<Role> roles;

    public static class Builder {
        private String username;
        private Set<Role> roles;

        public Builder() {
            roles = new HashSet<>();
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder role(Role role) {
            this.roles.add( role );
            return this;
        }

        public UserAuthorization build() {
            return new UserAuthorization( username, roles );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private UserAuthorization( @JsonProperty("username") String username,
                               @JsonProperty("roles") Set<Role> roles ) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean hasRole( Role role ) {
        Objects.requireNonNull( role );
        return roles.contains( role );
    }
}
