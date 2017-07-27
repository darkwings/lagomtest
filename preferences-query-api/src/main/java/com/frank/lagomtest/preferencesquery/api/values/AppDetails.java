package com.frank.lagomtest.preferencesquery.api.values;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.annotation.concurrent.Immutable;

/**
 * @author ftorriani
 */
@Immutable
public class AppDetails {

    public final String appId;
    public final String description;
    public final String creatorId;
    public final String status;

    public static class Builder {
        protected String appId;
        protected String description;
        protected String creatorId;
        protected String status;

        protected Builder() {
        }

        public Builder appId( String appId ) {
            this.appId = appId;
            return this;
        }

        public Builder description( String description ) {
            this.description = description;
            return this;
        }

        public Builder creatorId( String creatorId ) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder status( String status ) {
            this.status = status;
            return this;
        }

        public AppDetails build() {
            return new AppDetails( appId, description, creatorId, status );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    protected AppDetails( String appId, String description, String creatorId, String status ) {
        this.appId = appId;
        this.description = description;
        this.creatorId = creatorId;
        this.status = status;
    }

    public String getAppId() {
        return appId;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getStatus() {
        return status;
    }
}
