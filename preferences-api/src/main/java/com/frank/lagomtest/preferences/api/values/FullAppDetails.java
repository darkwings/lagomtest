package com.frank.lagomtest.preferences.api.values;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.frank.lagomtest.preferences.api.AppStatus;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

@Immutable
public final class FullAppDetails extends AppDetails {

	public final PSequence<BlockContainerDetail> blockContainers;
	
	public static class BlockContainerDetail {
		
		private final String blockContainerId;
		private final String description;
		// TODO: aggiungere tutti gli altri elementi

        public BlockContainerDetail( String blockContainerId, String description ) {
            this.blockContainerId = blockContainerId;
            this.description = description;
        }

        private BlockContainerDetail( Builder builder ) {
            blockContainerId = builder.blockContainerId;
            description = builder.description;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getBlockContainerId() {
			return blockContainerId;
		}

        public String getDescription() {
            return description;
        }


        public static final class Builder {
            private String blockContainerId;
            private String description;

            private Builder() {
            }

            public Builder blockContainerId( String blockContainerId ) {
                this.blockContainerId = blockContainerId;
                return this;
            }

            public Builder description( String description ) {
                this.description = description;
                return this;
            }

            public BlockContainerDetail build() {
                return new BlockContainerDetail( blockContainerId, description );
            }
        }
    }
	
	public static class FullBuilder extends Builder {
		
		private PSequence<BlockContainerDetail> blockContainers;
		
		protected FullBuilder() {
			super();
			blockContainers = TreePVector.empty();
		}
		
		public FullBuilder appDetails(AppDetails singleDetail) {
			this.appId = singleDetail.appId;
			this.description = singleDetail.description;
			this.creatorId = singleDetail.creatorId;
			this.status = singleDetail.status;
			this.portalContext = singleDetail.portalContext;
			return this;
		}
		
		public FullBuilder add( BlockContainerDetail blockContainer ) {
			Objects.requireNonNull( blockContainer );
			blockContainers = blockContainers.plus( blockContainer );
			return this;
		}
		
		public FullBuilder addAll( List<BlockContainerDetail> blockContainers ) {
			Objects.requireNonNull( blockContainers );
			this.blockContainers = this.blockContainers.plusAll( blockContainers );
			return this;
		}

		public FullAppDetails buildFull() {
			return new FullAppDetails(appId, description, creatorId, portalContext, status, blockContainers);
		}
	}
	
	
	@JsonCreator
	private FullAppDetails( String appId, String description, String creatorId, String portalContext,
			AppStatus status, PSequence<BlockContainerDetail> blockContainers ) {
		super( appId, description, creatorId, portalContext, status );
		this.blockContainers = blockContainers;
	}
	
	public static FullBuilder fullBuilder() {
		return new FullBuilder();
	}


	public List<BlockContainerDetail> getBlockContainers() {
		return blockContainers;
	}
	
}
