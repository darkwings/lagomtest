package com.frank.lagomtest.preferences.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.frank.lagomtest.preferences.api.App;
import com.frank.lagomtest.preferences.api.AppStatus;
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

    /**
     * {@link AppState} builder
     */
    public static class Builder {
        private App app;
        private AppStatus status;

        public Builder() {
        }

        public Builder app( App app ) {
            this.app = app;
            return this;
        }

        public Builder status( AppStatus status ) {
            this.status = status;
            return this;
        }

        public AppState build() {
            return new AppState( Optional.of( app ), status );
        }
    }

    @JsonCreator
    private AppState( Optional<App> app, AppStatus status ) {
        this.app = app;
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AppState notStarted() {
        return new AppState( Optional.empty(), AppStatus.DRAFT );
    }

    public static AppState start( App app ) {
        return new AppState( Optional.of( app ), AppStatus.ACTIVE );
    }

    public AppState withStatus( AppStatus status ) {
        return new AppState( app, status );
    }
}
