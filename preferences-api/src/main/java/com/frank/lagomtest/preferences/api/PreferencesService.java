package com.frank.lagomtest.preferences.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * @author ftorriani
 */
public interface PreferencesService extends Service {

    /**
     * Example: curl http://localhost:9000/api/preferences/echo/message
     */
    ServiceCall<NotUsed, String> echo( String message );

    /**
     * Example:
     * curl -X POST -d '{'"uniqueId":"my_new_app", "creatorId":"frank"}' http://localhost:9000/api/preferences/app/11221
     */
    ServiceCall<App, CreateAppResult> createApp( String appId );

    ServiceCall<NotUsed, AppDetails> getApp( String appId );

    ServiceCall<NotUsed, Done> activate( String appId );

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named( "preferences" ).withCalls(
                pathCall( "/api/preferences/app/:appId", this::createApp ),
                pathCall( "/api/preferences/app/:appId", this::getApp ),
                restCall( Method.POST, "/api/preferences/activate/:appId", this::activate ),
                pathCall( "/api/preferences/echo/:message", this::echo )
        ).withAutoAcl( true );
        // @formatter:on
    }
}
