package com.frank.lagomtest.preferences.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is an App
 *
 * @author ftorriani
 */
public final class App {

    private final String appId;

    private final String description;

    private final String creatorId;

    private final String portalContext;

    private final static App EMPTY = App.builder().
            appId( "" ).
            portalContext( "" ).
            description( "" ).
            creatorId( "" ).
            build();

    /**
     * @return an initial empty {@link App}
     */
    public static App empty() {
        return EMPTY;
    }


    @JsonCreator
    private App( @JsonProperty("appId") String appId,
                 @JsonProperty("description") String description,
                 @JsonProperty("creatorId") String creatorId,
                 @JsonProperty("portalContext") String portalContext ) {
        this.appId = appId;
        this.description = description;
        this.creatorId = creatorId;
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

    public String getPortalContext() {
        return portalContext;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return "".equals( appId ) &&
                "".equals( portalContext ) &&
                "".equals( description ) &&
                "".equals( creatorId );
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String appId;
        private String description;
        private String creatorId;
        private String portalContext;

        private Builder() {
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

        public App build() {
            return new App( appId, description, creatorId, portalContext );
        }
    }
}
