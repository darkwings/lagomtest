package com.frank.lagomtest.preferences.api.values;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.annotation.concurrent.Immutable;

/**
 * @author ftorriani
 */
@Immutable
public final class CreateAppDone {

    private final String appId;

    @JsonCreator
    private CreateAppDone( String appId ) {
        this.appId = appId;
    }

    public static CreateAppDone from( String appId ) {
        return new CreateAppDone( appId );
    }


    public String getAppId() {
        return appId;
    }
}
