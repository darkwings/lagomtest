package com.frank.lagomtest.preferences.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import com.lightbend.lagom.serialization.CompressedJsonable;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.Objects;
import java.util.Optional;

/**
 * Persistent state of an application
 *
 * @author ftorriani
 */
public final class AppState implements CompressedJsonable {

    /**
     * The App details. When the {@link AppState} is created for an not existing App,
     * the {@link App} object is not defined, so we declare it as an optional
     */
    private final Optional<App> app;

    /**
     * The App status
     */
    private final AppStatus status;

    /**
     * The {@link BlockContainer}s
     */
    private final PSequence<BlockContainer> blockContainers;

    @JsonCreator
    private AppState( @JsonProperty("app") Optional<App> app,
                      @JsonProperty("status") AppStatus status,
                      @JsonProperty("blockContainers") PSequence<BlockContainer> blockContainers ) {
        this.app = app;
        this.status = status;
        this.blockContainers = blockContainers;
    }

    public Optional<App> getApp() {
        return app;
    }

    public AppStatus getStatus() {
        return status;
    }

    public PSequence<BlockContainer> getBlockContainers() {
        return blockContainers;
    }

    public Optional<BlockContainer> getById( String blockContainerId ) {
        return Utils.iteratorToStream( blockContainers.iterator(), false ).
                filter( b -> blockContainerId.equals( b.getBlockContainerId() ) ).findFirst();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return !app.isPresent() || app.get().isEmpty();
    }

    /**
     * @return an empty {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a builder starting from previous state, ready to collect updates to
     * produce a new {@link AppState}.
     *
     * @param previous the previous state
     * @return a {@link Builder} initialized with the previous {@link AppState}
     */
    public static Builder builder( AppState previous ) {
        if ( previous.isEmpty() ) {
            throw new IllegalStateException( "Cannot instantiate a Builder from an empty state" );
        }

        Builder b = new Builder();
        b.app = previous.app.get();
        b.status = previous.status;
        b.blockContainers = previous.blockContainers;
        return b;
    }

    /**
     * {@link AppState} builder
     */
    public static class Builder {
        private App app;
        private AppStatus status;
        private PSequence<BlockContainer> blockContainers;

        public Builder() {
            blockContainers = TreePVector.empty();
        }

        public Builder app( App app ) {
            this.app = app;
            return this;
        }

        public Builder status( AppStatus status ) {
            this.status = status;
            return this;
        }

        public Builder add( BlockContainer container ) {
            Objects.requireNonNull( container );
            blockContainers = blockContainers.plus( container );
            return this;
        }

        public Builder remove( BlockContainer container ) {
            Objects.requireNonNull( container );
            blockContainers = blockContainers.minus( container );
            return this;
        }

        public AppState build() {
            return new AppState( Optional.of( app ), status, blockContainers );
        }
    }
}
