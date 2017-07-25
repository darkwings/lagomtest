/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package com.frank.lagomtest.stream.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.frank.lagomtest.stream.api.StreamService;
import com.frank.lagomtest.preferences.api.PreferencesService;
import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the StreamService.
 */
public class StreamServiceImpl implements StreamService {

    private final PreferencesService preferencesService;

    @Inject
    public StreamServiceImpl(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @Override
    public ServiceCall<NotUsed, Source<String, NotUsed>> appEvents() {
        return request -> 
        		    completedFuture( preferencesService.preferencesTopic().
        		    		subscribe().atMostOnceSource().map( evt -> {
        		    			System.out.println( "StreamServiceImpl.appEvents: received event " + evt ); 
        		    			return evt.getAppId() + " - " + evt.getMessage(); 
        		    		}) );        
    }

    @Override
    public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> stream() {
        throw new UnsupportedOperationException("Unsupported");
    }
}
