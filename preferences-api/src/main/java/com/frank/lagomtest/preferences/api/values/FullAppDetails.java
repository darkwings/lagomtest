package com.frank.lagomtest.preferences.api.values;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.frank.lagomtest.preferences.api.AppStatus;

@Immutable
public final class FullAppDetails extends AppDetails {

	public final List<BlockContainerDetail> blockContainers;
	
	public static class BlockContainerDetail {
		
		private final String blockContainerId;
		
		private BlockContainerDetail( String blockContainerId ) {
			this.blockContainerId = blockContainerId;
		}
		
		public String getBlockContainerId() {
			return blockContainerId;
		}
		
		public static BlockContainerDetail from( String blockContainerId ) {
			return new BlockContainerDetail( blockContainerId );
		}
	}
	
	public static class FullBuilder extends AppDetails.Builder {
		
		private List<BlockContainerDetail> blockContainers;
		
		protected FullBuilder() {
			super();
			blockContainers = new ArrayList<>();
		}
		
		public FullBuilder appDetails(AppDetails singleDetail) {
			this.appId = singleDetail.appId;
			this.description = singleDetail.description;
			this.creatorId = singleDetail.creatorId;
			this.status = singleDetail.status;
			return this;
		}
		
		public FullBuilder add( BlockContainerDetail blockContainer ) {
			Objects.requireNonNull( blockContainer );
			blockContainers.add( blockContainer );
			return this;
		}
		
		public FullBuilder addAll( List<BlockContainerDetail> blockContainers ) {
			Objects.requireNonNull( blockContainers );
			blockContainers.addAll( blockContainers );
			return this;
		}

		public FullAppDetails buildFull() {
			return new FullAppDetails(appId, description, creatorId, status, blockContainers);
		}
	}
	
	
	@JsonCreator
	private FullAppDetails( String appId, String description, String creatorId, 
			AppStatus status, List<BlockContainerDetail> blockContainers ) {
		super( appId, description, creatorId, status );
		this.blockContainers = blockContainers;
	}
	
	public static FullBuilder fullBuilder() {
		return new FullBuilder();
	}


	public List<BlockContainerDetail> getBlockContainers() {
		return blockContainers;
	}
	
}
