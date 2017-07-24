package com.frank.lagomtest.preferences.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collection;
import java.util.Collections;

/**
 * @author ftorriani
 */

public class BlockContainer {

    public final String blockContainerId;
    public final Collection<Block> blocks;

    @JsonCreator
    public BlockContainer( String blockContainerId, Collection<Block> blocks ) {
        this.blockContainerId = blockContainerId;
        this.blocks = Collections.unmodifiableCollection( blocks );
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
}
