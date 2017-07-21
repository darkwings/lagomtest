package com.frank.lagomtest.preferences.api;

/**
 * @author ftorriani
 */
public final class CreateAppResult {

    private final String appId;

    public CreateAppResult( String appId ) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }
}
