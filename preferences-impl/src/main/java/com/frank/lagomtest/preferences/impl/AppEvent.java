package com.frank.lagomtest.preferences.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.frank.lagomtest.preferences.api.model.App;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * @author ftorriani
 */
public interface AppEvent extends Jsonable, AggregateEvent<AppEvent> {

    //#sharded-tags

    int NUM_SHARDS = 4;

    AggregateEventShards<AppEvent> TAG = AggregateEventTag.sharded( AppEvent.class, NUM_SHARDS );

    @Override
    default AggregateEventTagger<AppEvent> aggregateTag() {
        return TAG;
    }

    //#sharded-tags

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppCreated implements AppEvent {
        public final String appId;
        public final App app;

        public static class Builder {
            private Builder() {
            }

            public String appId;
            public App app;

            public Builder appId( String appId ) {
                this.appId = appId;
                return this;
            }

            public Builder app( App app ) {
                this.app = app;
                return this;
            }

            public AppCreated build() {
                return new AppCreated( appId, app );
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @JsonCreator
        private AppCreated( String appId, App app ) {
            Objects.requireNonNull( appId );
            Objects.requireNonNull( app );
            this.appId = appId;
            this.app = app;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            AppCreated that = (AppCreated) o;

            return appId != null ? appId.equals( that.appId ) : that.appId == null;
        }

        @Override
        public int hashCode() {
            return appId != null ? appId.hashCode() : 0;
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppDeactivated implements AppEvent {

        public final String appId;

        @JsonCreator
        private AppDeactivated( String appId ) {
            this.appId = appId;
        }

        public static AppDeactivated from( String appId ) {
            Objects.requireNonNull( appId );
            return new AppDeactivated( appId );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            AppDeactivated that = (AppDeactivated) o;

            return appId != null ? appId.equals( that.appId ) : that.appId == null;
        }

        @Override
        public int hashCode() {
            return appId != null ? appId.hashCode() : 0;
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppActivated implements AppEvent {

        public final String appId;

        public static AppActivated from( String appId ) {
            Objects.requireNonNull( appId );
            return new AppActivated( appId );
        }

        @JsonCreator
        private AppActivated( String appId ) {
            this.appId = appId;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            AppDeactivated that = (AppDeactivated) o;

            return appId != null ? appId.equals( that.appId ) : that.appId == null;
        }

        @Override
        public int hashCode() {
            return appId != null ? appId.hashCode() : 0;
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppCancelled implements AppEvent {

        public final String appId;

        @JsonCreator
        private AppCancelled( String appId ) {
            this.appId = appId;
        }

        public static AppCancelled from( String appId ) {
            Objects.requireNonNull( appId );
            return new AppCancelled( appId );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            AppDeactivated that = (AppDeactivated) o;

            return appId != null ? appId.equals( that.appId ) : that.appId == null;
        }

        @Override
        public int hashCode() {
            return appId != null ? appId.hashCode() : 0;
        }
    }


    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class BlockContainerAdded implements AppEvent {

        public final String blockContainerId;
        public final String appId;

        @JsonCreator
        private BlockContainerAdded( String appId, String blockContainerId ) {
            Objects.requireNonNull( appId );
            Objects.requireNonNull( blockContainerId );
            this.appId = appId;
            this.blockContainerId = blockContainerId;
        }

        public static class Builder {
            private String blockContainerId;
            private String appId;

            private Builder() {
            }

            public Builder blockContainerId( String blockContainerId ) {
                this.blockContainerId = blockContainerId;
                return this;
            }

            public Builder appId( String appId ) {
                this.appId = appId;
                return this;
            }

            BlockContainerAdded build() {
                return new BlockContainerAdded( appId, blockContainerId );
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            BlockContainerAdded that = (BlockContainerAdded) o;

            return blockContainerId != null ?
                    blockContainerId.equals( that.blockContainerId ) : that.blockContainerId == null;
        }

        @Override
        public int hashCode() {
            return blockContainerId != null ? blockContainerId.hashCode() : 0;
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class BlockContainerRemoved implements AppEvent {

        public final String blockContainerId;
        public final String appId;

        public static class Builder {
            private String blockContainerId;
            private String appId;

            private Builder() {
            }

            public Builder blockContainerId( String blockContainerId ) {
                this.blockContainerId = blockContainerId;
                return this;
            }

            public Builder appId( String appId ) {
                this.appId = appId;
                return this;
            }

            BlockContainerRemoved build() {
                return new BlockContainerRemoved( appId, blockContainerId );
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @JsonCreator
        private BlockContainerRemoved( String appId, String blockContainerId ) {
            Objects.requireNonNull( appId );
            Objects.requireNonNull( blockContainerId );
            this.appId = appId;
            this.blockContainerId = blockContainerId;
        }


        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            BlockContainerRemoved that = (BlockContainerRemoved) o;

            return blockContainerId != null ?
                    blockContainerId.equals( that.blockContainerId ) : that.blockContainerId == null;
        }

        @Override
        public int hashCode() {
            return blockContainerId != null ? blockContainerId.hashCode() : 0;
        }
    }
}
