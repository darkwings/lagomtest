package com.frank.lagomtest.preferences.api;


/**
 * @author ftorriani
 */
public class AppDetails {

    public final String appId;
    public final App app;
    public final AppStatus status;

    public static class Builder {
        public String appId;
        public App app;
        public AppStatus status;

        private Builder() {
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder app(App app) {
            this.app = app;
            return this;
        }

        public Builder status(AppStatus status) {
            this.status = status;
            return this;
        }

        public AppDetails build() {
            return new AppDetails( appId, app, status );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private AppDetails( String appId, App app, AppStatus status ) {
        this.appId = appId;
        this.app = app;
        this.status = status;
    }
}
