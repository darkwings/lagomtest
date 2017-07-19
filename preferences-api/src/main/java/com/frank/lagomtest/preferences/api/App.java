package com.frank.lagomtest.preferences.api;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * This is an App
 *
 * @author ftorriani
 */
public class App {

    private final String uniqueId;

    private final String creatorId;

    @JsonCreator
    public App( String uniqueId, String creatorId ) {
        this.uniqueId = uniqueId;
        this.creatorId = creatorId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getCreatorId() {
        return creatorId;
    }
}
