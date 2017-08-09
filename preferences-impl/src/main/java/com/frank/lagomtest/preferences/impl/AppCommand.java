package com.frank.lagomtest.preferences.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import com.frank.lagomtest.preferences.api.values.AppDetails;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import org.pcollections.PSequence;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ftorriani
 */
public interface AppCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class CreateApp implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<CreateAppDone> {
        public final App app;

        @JsonCreator
        private CreateApp( App app ) {

            this.app = app;
        }

        public static CreateApp from( App app ) {
            return new CreateApp( app );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            CreateApp createApp = (CreateApp) o;

            return app != null ? app.equals( createApp.app ) : createApp.app == null;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder( "CreateApp{" );
            sb.append( "app=" ).append( app );
            sb.append( '}' );
            return sb.toString();
        }

        @Override
        public int hashCode() {
            return app != null ? app.hashCode() : 0;
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    final class CreateAppDone {

        private final String appId;

        public static CreateAppDone from( String appId ) {
            return new CreateAppDone( appId );
        }

        @JsonCreator
        private CreateAppDone( String appId ) {
            this.appId = appId;
        }

        public String getAppId() {
            return appId;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            CreateAppDone that = (CreateAppDone) o;

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
    class GetApp implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<AppDetails> {

        @JsonCreator
        private GetApp() {
        }

        public static GetApp build() {
            return new GetApp();
        }

        @Override
        public boolean equals( @Nullable Object another ) {
            return this instanceof GetApp;
        }

        @Override
        public int hashCode() {
            return 2053226012;
        }
    }

//    @SuppressWarnings("serial")
//    @Immutable
//    @JsonDeserialize
//    class GetAppResponse implements Jsonable {
//        final Optional<App> app;
//        final AppStatus status;
//        final PSequence<BlockContainer> blockContainers;
//
//        public static class Builder {
//            Optional<App> app;
//            AppStatus status;
//            PSequence<BlockContainer> blockContainers;
//
//            private Builder() {
//            }
//
//            public Builder app( Optional<App> app ) {
//                this.app = app;
//                return this;
//            }
//
//            public Builder blockContainers( PSequence<BlockContainer> blockContainers ) {
//                this.blockContainers = blockContainers;
//                return this;
//            }
//
//            public Builder status( AppStatus status ) {
//                this.status = status;
//                return this;
//            }
//
//            public GetAppResponse build() {
//                return new GetAppResponse( app, status, blockContainers );
//            }
//        }
//
//        private GetAppResponse( Optional<App> app, AppStatus status, PSequence<BlockContainer> blockContainers ) {
//            this.app = app;
//            this.status = status;
//            this.blockContainers = blockContainers;
//        }
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public PSequence<BlockContainer> getBlockContainers() {
//            return blockContainers;
//        }
//
//        public Optional<App> getApp() {
//            return app;
//        }
//
//        public AppStatus getStatus() {
//            return status;
//        }
//
//        @Override
//        public boolean equals( Object o ) {
//            if ( this == o ) {
//                return true;
//            }
//            if ( o == null || getClass() != o.getClass() ) {
//                return false;
//            }
//
//            GetAppResponse reply = (GetAppResponse) o;
//
//            if ( app != null ? !app.equals( reply.app ) : reply.app != null ) {
//                return false;
//            }
//            return status == reply.status;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = app != null ? app.hashCode() : 0;
//            result = 31 * result + ( status != null ? status.hashCode() : 0 );
//            return result;
//        }
//    }

    /**
     * Activates an App
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class ActivateApp implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {


        @JsonCreator
        private ActivateApp() {
        }

        public static ActivateApp build() {
            return new ActivateApp();
        }

        @Override
        public boolean equals( @Nullable Object another ) {
            return this instanceof ActivateApp;
        }

        @Override
        public int hashCode() {
            return 328648271;
        }
    }

    /**
     * Deactivates an App
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class DeactivateApp implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {

        @JsonCreator
        private DeactivateApp() {
        }

        public static DeactivateApp build() {
            return new DeactivateApp();
        }

        @Override
        public boolean equals( @Nullable Object another ) {
            return this instanceof DeactivateApp;
        }

        @Override
        public int hashCode() {
            return 8741023;
        }
    }

    /**
     * Cancel an App
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class CancelApp implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {


        @JsonCreator
        private CancelApp() {
        }

        public static CancelApp build() {
            return new CancelApp();
        }

        @Override
        public boolean equals( @Nullable Object another ) {
            return this instanceof CancelApp;
        }

        @Override
        public int hashCode() {
            return 140581230;
        }
    }


    /**
     * Adds a add container to an {@link AppEntity}
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AddBlockContainer implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {

        public final BlockContainer blockContainer;

        @JsonCreator
        private AddBlockContainer( BlockContainer blockContainer ) {
            this.blockContainer = blockContainer;
        }

        public static AddBlockContainer from( BlockContainer blockContainer ) {
            Objects.requireNonNull( blockContainer );
            return new AddBlockContainer( blockContainer );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            AddBlockContainer that = (AddBlockContainer) o;

            return blockContainer != null ? blockContainer.equals( that.blockContainer ) : that.blockContainer == null;
        }

        @Override
        public int hashCode() {
            return blockContainer != null ? blockContainer.hashCode() : 0;
        }
    }

    /**
     * Removes a add container to an {@link AppEntity}
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class RemoveBlockContainer implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {

        public final String blockContainerId;

        @JsonCreator
        private RemoveBlockContainer( String blockContainerId ) {
            this.blockContainerId = blockContainerId;
        }

        public static RemoveBlockContainer from( String blockContainerId ) {
            Objects.requireNonNull( blockContainerId );
            return new RemoveBlockContainer( blockContainerId );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            RemoveBlockContainer that = (RemoveBlockContainer) o;

            return blockContainerId != null ? blockContainerId.equals( that.blockContainerId ) : that.blockContainerId == null;
        }

        @Override
        public int hashCode() {
            return blockContainerId != null ? blockContainerId.hashCode() : 0;
        }
    }
}
