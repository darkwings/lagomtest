package com.frank.lagomtest.preferences.impl;

import java.util.Collection;
import java.util.Collections;

/**
 * @author ftorriani
 */
public class Block {

    public final String blockId;
    public final Collection<Widget> widgets;

    public Block( String blockId, Collection<Widget> widgets ) {
        this.blockId = blockId;
        this.widgets = Collections.unmodifiableCollection( widgets );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        Block block = (Block) o;

        return blockId != null ? blockId.equals( block.blockId ) : block.blockId == null;
    }

    @Override
    public int hashCode() {
        return blockId != null ? blockId.hashCode() : 0;
    }
}
