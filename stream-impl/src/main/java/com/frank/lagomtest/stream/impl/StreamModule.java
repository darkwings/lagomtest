/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package com.frank.lagomtest.stream.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.frank.lagomtest.preferences.api.PreferencesService;
//import com.frank.lagomtest.hello.api.HelloService;
import com.frank.lagomtest.stream.api.StreamService;

/**
 * The module that binds the StreamService so that it can be served.
 */
public class StreamModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    // Bind the StreamService service
    bindService( StreamService.class, StreamServiceImpl.class );
    
    // Bind the PreferencesService client
    bindClient( PreferencesService.class );
  }
}
