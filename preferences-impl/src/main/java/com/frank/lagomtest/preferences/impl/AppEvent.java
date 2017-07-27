package com.frank.lagomtest.preferences.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.frank.lagomtest.preferences.api.model.App;
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

    public String getAppId();
    
    public String getEventName();
    
    @SuppressWarnings("serial")
    abstract class AbstractAppEvent implements AppEvent {
    		public final String appId;

		public AbstractAppEvent(String appId) {
			super();
			Objects.requireNonNull( appId ); 
			this.appId = appId;
		}

		public String getAppId() {
			return appId;
		}
    }
    
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppCreated extends AbstractAppEvent {     
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
        

        @Override
		public String getEventName() {
			return "AppCreated";
		}

		public static Builder builder() {
            return new Builder();
        }

        @JsonCreator
        private AppCreated( @JsonProperty("appId") String appId, @JsonProperty("app") App app ) {
        		super( appId );           
            Objects.requireNonNull( app );           
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

		@Override
		public String toString() {
			StringBuilder builder2 = new StringBuilder();
			builder2.append("AppCreated [appId=");
			builder2.append(appId);
			builder2.append(", app=");
			builder2.append(app);
			builder2.append("]");
			return builder2.toString();
		}
        
        
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppDeactivated extends AbstractAppEvent {

        public final String appId;

        @JsonCreator
        private AppDeactivated( @JsonProperty("appId") String appId ) {
        		super( appId );
            this.appId = appId;
        }

        public static AppDeactivated from( String appId ) {
            Objects.requireNonNull( appId );
            return new AppDeactivated( appId );
        }
        
        @Override
		public String getEventName() {
			return "AppDeactivated";
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

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AppDeactivated [appId=");
			builder.append(appId);
			builder.append("]");
			return builder.toString();
		}
        
        
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppActivated extends AbstractAppEvent {

        public final String appId;

        public static AppActivated from( String appId ) {
            Objects.requireNonNull( appId );
            return new AppActivated( appId );
        }           

		@JsonCreator
        private AppActivated( @JsonProperty("appId") String appId ) {
			super( appId );
            this.appId = appId;
        }
		
		@Override
		public String getEventName() {
			return "AppActivated";
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

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AppActivated [appId=");
			builder.append(appId);
			builder.append("]");
			return builder.toString();
		}
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class AppCancelled extends AbstractAppEvent {

        public final String appId;

        @JsonCreator
        private AppCancelled( String appId ) {
        	    super( appId );
            this.appId = appId;
        }

        public static AppCancelled from( String appId ) {
            Objects.requireNonNull( appId );
            return new AppCancelled( appId );
        }
        
        @Override
		public String getEventName() {
			return "AppCancelled";
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

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AppCancelled [appId=");
			builder.append(appId);
			builder.append("]");
			return builder.toString();
		}
        
    }


    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class BlockContainerAdded extends AbstractAppEvent {

        public final String blockContainerId;
       

        @JsonCreator
        private BlockContainerAdded( @JsonProperty("appId") String appId, 
        								@JsonProperty("blockContainerId") String blockContainerId ) {
            super( appId );
            Objects.requireNonNull( blockContainerId );           
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

            public BlockContainerAdded build() {
                return new BlockContainerAdded( appId, blockContainerId );
            }
        }

        public static Builder builder() {
            return new Builder();
        }
        
        @Override
		public String getEventName() {
			return "BlockContainerAdded";
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

		@Override
		public String toString() {
			StringBuilder builder2 = new StringBuilder();
			builder2.append("BlockContainerAdded [blockContainerId=");
			builder2.append(blockContainerId);
			builder2.append(", appId=");
			builder2.append(appId);
			builder2.append("]");
			return builder2.toString();
		}
        
        
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class BlockContainerRemoved extends AbstractAppEvent {

        public final String blockContainerId;        

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

            public BlockContainerRemoved build() {
                return new BlockContainerRemoved( appId, blockContainerId );
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @JsonCreator
        private BlockContainerRemoved( @JsonProperty("appId") String appId, @JsonProperty("blockContainerId") String blockContainerId ) {
            super( appId );
            Objects.requireNonNull( blockContainerId );
            this.blockContainerId = blockContainerId;
        }

        @Override
		public String getEventName() {
			return "BlockContainerRemoved";
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

		@Override
		public String toString() {
			StringBuilder builder2 = new StringBuilder();
			builder2.append("BlockContainerRemoved [blockContainerId=");
			builder2.append(blockContainerId);
			builder2.append(", appId=");
			builder2.append(appId);
			builder2.append("]");
			return builder2.toString();
		}        
    }
}
