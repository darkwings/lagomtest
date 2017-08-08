package com.frank.lagomtest.preferences.impl.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

/**
 * Persistent status of an application
 *
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
    public final Optional<AppStatus> status;

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
            return new AppState( Optional.of( app ), Optional.of( status ) );
        }
    }

    @JsonCreator
    private AppState( @JsonProperty("app") Optional<App> app,
                      @JsonProperty("status") Optional<AppStatus> status ) {
        this.app = app;
        this.status = status;
    }

    /**
     * @return a {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return !app.isPresent() || app.get().isEmpty();
    }

    /**
     * Creates a builder starting from previous state, getting the {@link App}
     *
     * @param previousState the previous state
     * @return a {@link Builder} initialized with the current {@link App}
     */
    public static Builder builder( AppState previousState ) {
        if ( previousState.isEmpty() ) {
            throw new IllegalStateException( "Cannot instantiate a Builder from an empty state" );
        }

        Builder b = new Builder();
        b.app = previousState.app.get();
        return b;
    }

    public Optional<App> getApp() {
        return app;
    }

    public Optional<AppStatus> getStatus() {
        return status;
    }
}
