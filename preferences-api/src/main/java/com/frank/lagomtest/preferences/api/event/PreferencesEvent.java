package com.frank.lagomtest.preferences.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;

/**
 * Evento generico sul servizio delle preferenze, che viene
 * inviato ad un topic Kafka
 *
 * @author ftorriani
 */
@Immutable
@JsonDeserialize
public class PreferencesEvent {

    private final String appId;
    private final String message;

    public static class Builder {
        private String appId;
        private String message;

        private Builder() {
        }

        public Builder appId( String appId ) {
            this.appId = appId;
            return this;
        }

        public Builder message( String message ) {
            this.message = message;
            return this;
        }

        public PreferencesEvent build() {
            return new PreferencesEvent( appId, message );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private PreferencesEvent( String appId, String message ) {
        this.appId = appId;
        this.message = message;
    }

    public String getAppId() {
        return appId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PreferencesEvent that = (PreferencesEvent) o;

        if ( appId != null ? !appId.equals( that.appId ) : that.appId != null ) {
            return false;
        }
        return message != null ? message.equals( that.message ) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = appId != null ? appId.hashCode() : 0;
        result = 31 * result + ( message != null ? message.hashCode() : 0 );
        return result;
    }
}
