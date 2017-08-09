package com.frank.lagomtest.preferences.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author ftorriani
 */

public class BlockContainer {

    private final String blockContainerId;
    private final String description;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final boolean iconizable;
    private final PSequence<Block> blocks;

    public static class Builder {

        private String blockContainerId;
        private String description;
        private int x;
        private int y;
        private int width;
        private int height;
        private boolean iconizable;
        private PSequence<Block> blocks;

        private Builder() {
            blocks = TreePVector.empty();
        }

        public Builder blockContainerId( String blockContainerId ) {
            this.blockContainerId = blockContainerId;
            return this;
        }

        public Builder description( String description ) {
            this.description = description;
            return this;
        }

        public Builder x( int x ) {
            this.x = x;
            return this;
        }

        public Builder y( int y ) {
            this.y = y;
            return this;
        }

        public Builder width( int width ) {
            this.width = width;
            return this;
        }

        public Builder height( int height ) {
            this.height = height;
            return this;
        }

        public Builder iconizable( boolean iconizable ) {
            this.iconizable = iconizable;
            return this;
        }

        public Builder add( Block block ) {
            Objects.requireNonNull( block );
            blocks = blocks.plus( block );
            return this;
        }

        public Builder remove( Block block ) {
            Objects.requireNonNull( block );
            blocks = blocks.minus( block );
            return this;
        }

        public BlockContainer build() {
            return new BlockContainer( blockContainerId, description, x, y,
                    width, height, iconizable, blocks );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private BlockContainer( @JsonProperty("blockContainerId") String blockContainerId,
                            @JsonProperty("description") String description,
                            @JsonProperty("x") int x,
                            @JsonProperty("y") int y,
                            @JsonProperty("width") int width,
                            @JsonProperty("height") int height,
                            @JsonProperty("iconizable") boolean iconizable,
                            @JsonProperty("blocks") PSequence<Block> blocks ) {
        this.blockContainerId = blockContainerId;
        this.description = description;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.iconizable = iconizable;
        this.blocks = blocks;
    }

    public String getDescription() {
        return description;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isIconizable() {
        return iconizable;
    }

    public PSequence<Block> getBlocks() {
        return blocks;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( "BlockContainer{" );
        sb.append( "blockContainerId='" ).append( blockContainerId ).append( '\'' );
        sb.append( ", description='" ).append( description ).append( '\'' );
        sb.append( ", x=" ).append( x );
        sb.append( ", y=" ).append( y );
        sb.append( ", width=" ).append( width );
        sb.append( ", height=" ).append( height );
        sb.append( ", iconizable=" ).append( iconizable );
        sb.append( ", blocks=" ).append( blocks );
        sb.append( '}' );
        return sb.toString();
    }
}
