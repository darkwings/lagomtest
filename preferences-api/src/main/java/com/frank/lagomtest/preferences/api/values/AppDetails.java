package com.frank.lagomtest.preferences.api.values;

import com.frank.lagomtest.preferences.api.AppStatus;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class AppDetails {

    final App app;
    final AppStatus status;
    final PSequence<BlockContainer> blockContainers;

    public static class Builder {
        private App app;
        private AppStatus status;
        private PSequence<BlockContainer> blockContainers;

        private Builder() {
            blockContainers = TreePVector.empty();
        }

        public Builder app( App app ) {
            this.app = app;
            return this;
        }

        public Builder blockContainers( PSequence<BlockContainer> blockContainers ) {
            this.blockContainers = this.blockContainers.plusAll( blockContainers );
            return this;
        }

        public Builder status( AppStatus status ) {
            this.status = status;
            return this;
        }

        public AppDetails build() {
            return new AppDetails( app, status, blockContainers );
        }
    }

    private AppDetails( App app, AppStatus status, PSequence<BlockContainer> blockContainers ) {
        this.app = app;
        this.status = status;
        this.blockContainers = blockContainers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public PSequence<BlockContainer> getBlockContainers() {
        return blockContainers;
    }

    public App getApp() {
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

        AppDetails reply = (AppDetails) o;

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
