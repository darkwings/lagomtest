/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package com.frank.lagomtest.stream.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.frank.lagomtest.stream.api.StreamService;
import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferences.api.event.PreferencesEvent;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the StreamService.
 */
public class StreamServiceImpl implements StreamService {

    private final PreferencesService preferencesService;

    @Inject
    public StreamServiceImpl( PreferencesService preferencesService ) {
        this.preferencesService = preferencesService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceCall<NotUsed, Source<String, NotUsed>> appEvents() {
        return request -> {
					Source<PreferencesEvent, NotUsed> source = (Source<PreferencesEvent, NotUsed>) 
        		    		preferencesService.preferencesTopic().subscribe().atMostOnceSource();
					return completedFuture( source.map( evt ->  
        		    			evt.getAppId() + " - " + evt.getMessage() 
        		    ) );
        };
    }

    @Override
    public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> stream() {
        throw new UnsupportedOperationException( "Unsupported" );
    }
}
