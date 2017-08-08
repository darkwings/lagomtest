package com.frank.lagomtest.preferences.impl.blockcontainer;

import com.frank.lagomtest.preferences.api.model.BlockContainer;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Objects;
import java.util.Optional;

/**
 * @author ftorriani
 */
public class BlockContainerState implements CompressedJsonable {

    private final Optional<BlockContainer> blockContainer;

    private BlockContainerState( Optional<BlockContainer> blockContainer ) {
        this.blockContainer = blockContainer;
    }

    public static BlockContainerState from( BlockContainer blockContainer ) {
        Objects.requireNonNull( blockContainer );
        return new BlockContainerState( Optional.of( blockContainer ) );
    }

    public Optional<BlockContainer> getBlockContainer() {
        return blockContainer;
    }
}
