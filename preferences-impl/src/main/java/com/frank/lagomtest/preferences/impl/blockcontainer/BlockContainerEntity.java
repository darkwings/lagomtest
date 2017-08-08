package com.frank.lagomtest.preferences.impl.blockcontainer;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author ftorriani
 */
public class BlockContainerEntity extends PersistentEntity<BlockContainerCommand, BlockContainerEvent, BlockContainerState> {

    private final Logger log = LoggerFactory.getLogger( BlockContainerEntity.class );

    @Override
    public Behavior initialBehavior( Optional<BlockContainerState> snapshotState ) {
        return null;
    }
}
