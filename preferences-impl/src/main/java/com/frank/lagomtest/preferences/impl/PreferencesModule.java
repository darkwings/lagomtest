/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package com.frank.lagomtest.preferences.impl;

import com.frank.lagomtest.preferences.api.PreferencesService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * The module that binds the HelloService so that it can be served.
 */
public class PreferencesModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindService( PreferencesService.class, PreferencesServiceImpl.class );
    }
}
