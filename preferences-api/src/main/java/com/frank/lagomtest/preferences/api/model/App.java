package com.frank.lagomtest.preferences.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is an App
 *
 * @author ftorriani
 */
public final class App {

    private final String description;

    private final String creatorId;

    /**
     * @return an initial empty {@link App}
     */
    public static App empty() {
        return new App( "", "" );
    }

    @JsonCreator
    private App( @JsonProperty("description") String description, @JsonProperty("creatorId") String creatorId ) {
        this.description = description;
        this.creatorId = creatorId;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatorId() {
        return creatorId;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return "".equals( description ) && "".equals( creatorId );
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( "App{" );
        sb.append( "description='" ).append( description ).append( '\'' );
        sb.append( ", creatorId='" ).append( creatorId ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
