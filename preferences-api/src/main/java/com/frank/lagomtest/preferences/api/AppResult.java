package com.frank.lagomtest.preferences.api;

/**
 * @author ftorriani
 */
public final class AppResult {

    private final String appId;

    // TODO altre cose; da decidere

    public AppResult( String appId ) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }
}
