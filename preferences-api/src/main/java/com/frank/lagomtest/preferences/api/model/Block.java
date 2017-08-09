package com.frank.lagomtest.preferences.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author ftorriani
 */
public class Block {

    private final String blockId;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final PSequence<Widget> widgets;

    @JsonCreator
    private Block( @JsonProperty("blockId") String blockId,
                   @JsonProperty("x") int x,
                   @JsonProperty("y") int y,
                   @JsonProperty("width") int width,
                   @JsonProperty("height") int height,
                   @JsonProperty("widgets") PSequence<Widget> widgets ) {
        this.blockId = blockId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widgets = widgets;
    }

    public String getBlockId() {
        return blockId;
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

    public Collection<Widget> getWidgets() {
        return widgets;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String blockId;
        private int x;
        private int y;
        private int width;
        private int height;
        private PSequence<Widget> widgets;

        private Builder() {
            widgets = TreePVector.empty();
        }

        public Builder blockId( String blockId ) {
            this.blockId = blockId;
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

        public Builder widgets( Collection<Widget> widgets ) {
            Objects.requireNonNull( widgets );
            this.widgets.plusAll( widgets );
            return this;
        }

        public Builder widgets( Widget widget ) {
            widgets.plus( widget );
            return this;
        }

        public Block build() {
            return new Block( blockId, x, y, width, height, widgets );
        }
    }
}
