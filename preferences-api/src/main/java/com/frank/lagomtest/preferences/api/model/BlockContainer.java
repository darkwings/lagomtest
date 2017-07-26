package com.frank.lagomtest.preferences.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ftorriani
 */

public class BlockContainer {

    public final String blockContainerId;
    

    @JsonCreator
    private BlockContainer( @JsonProperty("blockContainerId") String blockContainerId ) {
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

        BlockContainer that = (BlockContainer) o;

        return blockContainerId != null ? blockContainerId.equals( that.blockContainerId ) : that.blockContainerId == null;
    }

    @Override
    public int hashCode() {
        return blockContainerId != null ? blockContainerId.hashCode() : 0;
    }

	public String getBlockContainerId() {
		return blockContainerId;
	}
    
    
}
