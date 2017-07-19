package com.frank.lagomtest.preferences.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.frank.lagomtest.preferences.api.App;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;

/**
 * @author ftorriani
 */
public interface AppEvent extends Jsonable, AggregateEvent<AppEvent> {

    int NUM_SHARDS = 4;

    AggregateEventShards<AppEvent> TAG = AggregateEventTag.sharded( AppEvent.class, NUM_SHARDS );

    @Override
    default AggregateEventTagger<AppEvent> aggregateTag() {
        return TAG;
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppCreated implements AppEvent {
        public final String appId;
        public final App app;

        public AppCreated( String appId, App app ) {
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

        public AppDeactivated( String appId ) {
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
    class AppActivated implements AppEvent {

        public final String appId;

        public AppActivated( String appId ) {
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

        public AppCancelled( String appId ) {
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
    class BlockContainerAdded implements AppEvent {

        public final String blockContainerId;
//        public final String appId;

        @JsonCreator
        public BlockContainerAdded( String blockContainerId ) {
            this.blockContainerId = Preconditions.checkNotNull( blockContainerId, "blockContainerId" );
            ;
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
//        public final String appId;

        @JsonCreator
        public BlockContainerRemoved( String blockContainerId ) {
            this.blockContainerId = Preconditions.checkNotNull( blockContainerId, "blockContainerId" );
            ;
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
