package com.frank.lagomtest.preferences.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.frank.lagomtest.preferences.api.App;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
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
        public CreateApp( App app ) {

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

        @JsonCreator
        public CreateAppDone( String appId ) {
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
            PersistentEntity.ReplyType<GetAppReply> {

        @Override
        public boolean equals(@Nullable Object another) {
            return this instanceof GetApp;
        }

        @Override
        public int hashCode() {
            return 2053226012;
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class GetAppReply implements Jsonable {
        final Optional<App> app;
        final AppStatus status;

        public GetAppReply( Optional<App> app, AppStatus status ) {
            this.app = app;
            this.status = status == null ? AppStatus.DRAFT : status;
        }

        public Optional<App> getApp() {
            return app;
        }

        public AppStatus getStatus() {
            return status;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            GetAppReply reply = (GetAppReply) o;

            if ( app != null ? !app.equals( reply.app ) : reply.app != null ) {
                return false;
            }
            return status == reply.status;
        }

        @Override
        public int hashCode() {
            int result = app != null ? app.hashCode() : 0;
            result = 31 * result + ( status != null ? status.hashCode() : 0 );
            return result;
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
        public final String appId;

        @JsonCreator
        public DeactivateApp( String appId ) {

            this.appId = Preconditions.checkNotNull( appId, "appId" );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            DeactivateApp that = (DeactivateApp) o;

            return appId != null ? appId.equals( that.appId ) : that.appId == null;
        }

        @Override
        public int hashCode() {
            return appId != null ? appId.hashCode() : 0;
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
        public final String appId;

        @JsonCreator
        public CancelApp( String appId ) {

            this.appId = Preconditions.checkNotNull( appId, "appId" );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            DeactivateApp that = (DeactivateApp) o;

            return appId != null ? appId.equals( that.appId ) : that.appId == null;
        }

        @Override
        public int hashCode() {
            return appId != null ? appId.hashCode() : 0;
        }
    }

    /**
     * Activates an App
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class ActivateApp implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {
        public final String appId;

        @JsonCreator
        public ActivateApp( String appId ) {

            this.appId = Preconditions.checkNotNull( appId, "appId" );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            DeactivateApp that = (DeactivateApp) o;

            return appId != null ? appId.equals( that.appId ) : that.appId == null;
        }

        @Override
        public int hashCode() {
            return appId != null ? appId.hashCode() : 0;
        }
    }

    /**
     * Adds a block container to an {@link AppEntity}
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AddBlockContainer implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {

        public final String blockContainerId;

        @JsonCreator
        public AddBlockContainer( String blockContainerId ) {

            this.blockContainerId = Preconditions.checkNotNull( blockContainerId, "blockContainerId" );
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

            return blockContainerId != null ? blockContainerId.equals( that.blockContainerId ) : that.blockContainerId == null;
        }

        @Override
        public int hashCode() {
            return blockContainerId != null ? blockContainerId.hashCode() : 0;
        }
    }

    /**
     * Removes a block container to an {@link AppEntity}
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class RemoveBlockContainer implements AppCommand, CompressedJsonable,
            PersistentEntity.ReplyType<Done> {

        public final String blockContainerId;

        @JsonCreator
        public RemoveBlockContainer( String blockContainerId ) {

            this.blockContainerId = Preconditions.checkNotNull( blockContainerId, "blockContainerId" );
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