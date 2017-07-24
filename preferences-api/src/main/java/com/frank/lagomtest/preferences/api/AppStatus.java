package com.frank.lagomtest.preferences.api;

/**
 * @author ftorriani
 */
public enum AppStatus {

    /**
     * The App is in its initial phase
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
