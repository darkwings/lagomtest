package com.frank.lagomtest.preferences.api;


/**
 * @author ftorriani
 */
public class AppDetails {

    public final String appId;
    public final App app;
    public final AppStatus status;

    public AppDetails( String appId, App app, AppStatus status ) {
        this.appId = appId;
        this.app = app;
        this.status = status;
    }
}
