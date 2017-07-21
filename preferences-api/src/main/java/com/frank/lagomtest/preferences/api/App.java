package com.frank.lagomtest.preferences.api;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * This is an App
 *
 * @author ftorriani
 */
public final class App {

    private final String uniqueId;

    private final String creatorId;

    public static class Builder {
        private String uniqueId;
        private String creatorId;

        public Builder() {
        }

        public Builder uniqueId( String uniqueId ) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder creatorId( String creatorId ) {
            this.creatorId = creatorId;
            return this;
        }

        public App build() {
            return new App( uniqueId, creatorId );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private App( String uniqueId, String creatorId ) {
        this.uniqueId = uniqueId;
        this.creatorId = creatorId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( "App{" );
        sb.append( "uniqueId='" ).append( uniqueId ).append( '\'' );
        sb.append( ", creatorId='" ).append( creatorId ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
