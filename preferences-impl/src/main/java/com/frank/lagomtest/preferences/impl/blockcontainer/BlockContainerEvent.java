package com.frank.lagomtest.preferences.impl.blockcontainer;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * @author ftorriani
 */
public interface BlockContainerEvent extends Jsonable, AggregateEvent<BlockContainerEvent> {

    //#sharded-tags

    int NUM_SHARDS = 4;

    AggregateEventShards<BlockContainerEvent> TAG = AggregateEventTag.sharded( BlockContainerEvent.class, NUM_SHARDS );

    @Override
    default AggregateEventTagger<BlockContainerEvent> aggregateTag() {
        return TAG;
    }

    //#sharded-tags
}
