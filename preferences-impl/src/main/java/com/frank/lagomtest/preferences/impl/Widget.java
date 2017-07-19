package com.frank.lagomtest.preferences.impl;

/**
 * @author ftorriani
 */
public class Widget {

    public final String widgetId;

    public Widget( String widgetId ) {
        this.widgetId = widgetId;
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
