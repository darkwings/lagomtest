package com.frank.lagomtest.preferences.api.values;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.AppStatus;

import javax.annotation.concurrent.Immutable;

/**
 * List of all Apps
 *
 * @author ftorriani
 */
@Immutable
public class AllApps {

    private final String appId;
    private final String description;
    private final String creatorId;
    private final String portalContext;
    private final AppStatus status;

    public static class Builder {
        protected String appId;
        protected String description;
        protected String creatorId;
        protected AppStatus status;
        protected String portalContext;

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

        public Builder portalContext( String portalContext ) {
            this.portalContext = portalContext;
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

        public AllApps build() {
            return new AllApps( appId, description, creatorId, portalContext, status );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    protected AllApps( String appId, String description, String creatorId, String portalContext, AppStatus status ) {
        this.appId = appId;
        this.description = description;
        this.creatorId = creatorId;
        this.status = status;
        this.portalContext = portalContext;
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

    public String getPortalContext() {
        return portalContext;
    }
}
