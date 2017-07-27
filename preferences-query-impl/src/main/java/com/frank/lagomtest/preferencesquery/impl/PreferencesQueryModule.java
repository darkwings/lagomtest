/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package com.frank.lagomtest.preferencesquery.impl;

import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferencesquery.api.PreferencesQueryService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * The module that binds the PreferencesService so that it can be served.
 */
public class PreferencesQueryModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {

        bindService( PreferencesQueryService.class, PreferencesQueryServiceImpl.class );        
        bindClient( PreferencesService.class );
    }
}
