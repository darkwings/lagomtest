package com.frank.lagomtest.preferencesquery.api;


import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.namedCall;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;
import org.pcollections.PSequence;

import com.frank.lagomtest.preferencesquery.api.values.AppDetails;
import com.frank.lagomtest.preferencesquery.api.values.FullAppDetails;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import akka.NotUsed;

public interface PreferencesQueryService extends Service {

	/**
     * @param appId
     * @return the {@link AppDetails details} of an {@link App}
     */
    ServiceCall<NotUsed, FullAppDetails> getApp( String appId );
    
    /**
     * @return a list of all registered apps
     */
    ServiceCall<NotUsed, PSequence<AppDetails>> getAllApps();
    
    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named( "preferences-query" ).withCalls(
                        pathCall( "/api/preferences-query/app/:appId", this::getApp ),
                        namedCall( "/api/preferences-query/app", this::getAllApps )
                ).
                withAutoAcl( true );
        // @formatter:on
    }
}
