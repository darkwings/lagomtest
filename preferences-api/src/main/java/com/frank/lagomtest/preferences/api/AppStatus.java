package com.frank.lagomtest.preferences.api;

/**
 * @author ftorriani
 */
public enum AppStatus {

    /**
     * The App is not started, or doesn't exist yet
     */
    DRAFT,

    /**
     * The App is active
     */
    ACTIVE,

    /**
     * The App is not active (can be activated or cancelled)
     */
    INACTIVE,


    /**
     * The App is cancelled
     */
    CANCELLED;
}
