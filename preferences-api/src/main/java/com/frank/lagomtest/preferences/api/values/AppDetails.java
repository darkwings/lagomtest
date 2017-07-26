package com.frank.lagomtest.preferences.api.values;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.AppStatus;

import javax.annotation.concurrent.Immutable;

/**
 * @author ftorriani
 */
@Immutable
public class AppDetails {

    public final String appId;
    public final String description;
    public final String creatorId;
    public final AppStatus status;

    public static class Builder {
        protected String appId;
        protected String description;
        protected String creatorId;
        protected AppStatus status;

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

        public Builder app( App app ) {
            this.description = app.getDescription();
            this.creatorId = app.getCreatorId();
            return this;
        }

        public Builder status( AppStatus status ) {
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
    protected AppDetails( String appId, String description, String creatorId, AppStatus status ) {
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

    public AppStatus getStatus() {
        return status;
    }
}
