package com.frank.lagomtest.preferences.api.values;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.annotation.concurrent.Immutable;

/**
 * @author ftorriani
 */
@Immutable
public final class CreateAppResult {

    private final String appId;

    @JsonCreator
    private CreateAppResult( String appId ) {
        this.appId = appId;
    }

    public static CreateAppResult from( String appId ) {
        return new CreateAppResult( appId );
    }


    public String getAppId() {
        return appId;
    }
}
