package com.frank.lagomtest.preferences.impl.blockcontainer;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import org.pcollections.PSequence;

/**
 * @author ftorriani
 */
public class BlockContainerEventProcessor extends ReadSideProcessor<BlockContainerEvent> {

    @Override
    public ReadSideHandler<BlockContainerEvent> buildHandler() {
        return null;
    }

    @Override
    public PSequence<AggregateEventTag<BlockContainerEvent>> aggregateTags() {
        return null;
    }
}
