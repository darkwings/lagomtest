package com.frank.lagomtest.preferences.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.frank.lagomtest.preferences.api.App;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

/**
 * @author ftorriani
 */
public final class AppState implements CompressedJsonable {

    /**
     * The App details
     */
    public final Optional<App> app;

    /**
     * The App status
     */
    public final AppStatus status;

    @JsonCreator
    public AppState( Optional<App> app, AppStatus status ) {
        this.app = app;
        this.status = status;
    }

    public static AppState notStarted() {
        return new AppState( Optional.empty(), AppStatus.NOT_STARTED );
    }

    public static AppState start( App app ) {
        return new AppState( Optional.of( app ), AppStatus.ACTIVE );
    }

    public AppState withStatus( AppStatus status ) {
        return new AppState( app, status );
    }
}
