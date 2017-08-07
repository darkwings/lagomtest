package com.frank.lagomtest.preferences.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ftorriani
 */
public class Widget {

    public final String widgetId;
    public final int x;
    public final int y;
    public final String type;
    public final String contentType;

    @JsonCreator
    public Widget( @JsonProperty("widgetId") String widgetId,
                   @JsonProperty("x") int x,
                   @JsonProperty("y") int y,
                   @JsonProperty("type") String type,
                   @JsonProperty("contentType") String contentType ) {
        this.widgetId = widgetId;
        this.x = x;
        this.y = y;
        this.type = type;
        this.contentType = contentType;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        Widget widget = (Widget) o;

        return widgetId != null ? widgetId.equals( widget.widgetId ) : widget.widgetId == null;
    }

    @Override
    public int hashCode() {
        return widgetId != null ? widgetId.hashCode() : 0;
    }
}
