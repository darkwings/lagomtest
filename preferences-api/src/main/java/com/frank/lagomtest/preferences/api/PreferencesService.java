package com.frank.lagomtest.preferences.api;

import akka.Done;
import akka.NotUsed;
import com.frank.lagomtest.preferences.api.event.PreferencesEvent;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import com.frank.lagomtest.preferences.api.values.AppDetails;
import com.frank.lagomtest.preferences.api.values.CreateAppResult;
import com.frank.lagomtest.preferences.api.values.FullAppDetails;
import com.lightbend.lagom.javadsl.api.CircuitBreaker;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.PSequence;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * @author ftorriani
 */
public interface PreferencesService extends Service {

    String PREFERENCES_TOPIC = "preferences";

    /**
     * Example: curl http://localhost:9000/api/preferences/echo/[message]
     */
    ServiceCall<NotUsed, String> echo( String message );

    /**
     * Example:
     * curl -X POST -d '{'"description":"my new app", "creatorId":"frank"}' http://localhost:9000/api/preferences/app/11221
     */
    ServiceCall<App, CreateAppResult> createApp( String appId );

    /**
     * @param appId
     * @return the {@link AppDetails details} of an {@link App}
     */
    ServiceCall<NotUsed, FullAppDetails> getApp( String appId );

    /**
     * Change the status of an {@link App} to {@link AppStatus#ACTIVE}
     *
     * @param appId the app id
     * @return a {@link ServiceCall}
     */
    ServiceCall<NotUsed, Done> activate( String appId );

    /**
     * Change the status of an {@link App} to {@link AppStatus#INACTIVE}
     *
     * @param appId the app id
     * @return a {@link ServiceCall}
     */
    ServiceCall<NotUsed, Done> deactivate( String appId );

    /**
     * Change the status of an {@link App} to {@link AppStatus#CANCELLED}
     *
     * @param appId the app id
     * @return a {@link ServiceCall}
     */
    ServiceCall<NotUsed, Done> cancel( String appId );

    /**
     * @return a list of all registered apps
     */
    ServiceCall<NotUsed, PSequence<AppDetails>> getAllApps();
    
    /**
     * Adds a {@link BlockContainer} to an {@link App}
     * @param appId the app id
     * @return a {@link ServiceCall}
     */
    ServiceCall<BlockContainer, Done> addBlockContainer( String appId );
    
    /**
     * Remove a {@link BlockContainer}. It is deleted from the database.
     * 
     * Given that this is a POC, no check is performed on the validity of the block ID
     * 
     * @param blockContainerId the id of the {@link BlockContainer}
     * 
     * @return a {@link ServiceCall}
     */
    ServiceCall<NotUsed, Done> removeBlockContainer( String appId, String blockContainerId );

    /**
     * @return the topic handle
     */
    Topic<PreferencesEvent> preferencesTopic();

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named( "preferences" ).withCalls(
                        pathCall( "/api/preferences/app/:appId", this::getApp ).
                                withCircuitBreaker( CircuitBreaker.identifiedBy( "preferences-get" ) ),
                        namedCall( "/api/preferences/app", this::getAllApps ).
                                withCircuitBreaker( CircuitBreaker.identifiedBy( "preferences-get" ) ),
                        pathCall( "/api/preferences/app/:appId", this::createApp ),
                        restCall( Method.POST, "/api/preferences/activate/:appId", this::activate ),
                        restCall( Method.POST, "/api/preferences/deactivate/:appId", this::deactivate ),
                        restCall( Method.POST, "/api/preferences/cancel/:appId", this::cancel ),
                        restCall( Method.POST, "/api/preferences/app/:appId/addBlockContainer", this::addBlockContainer ),
                        restCall( Method.DELETE, "/api/preferences/app/:appId/deleteBlockContainer/:blockId", this::removeBlockContainer ),
                        pathCall( "/api/preferences/echo/:message", this::echo )
                ).
                withTopics(
                        topic( PREFERENCES_TOPIC, this::preferencesTopic )
                                // Fornendo una partition key basata su appId, ci assicuriamo
                                // che tutti gli eventi che riguardano una precisa entità arriveranno
                                // tutti sullo stesso topic
                                .withProperty( KafkaProperties.partitionKeyStrategy(), PreferencesEvent::getAppId )
                ).
                withAutoAcl( true );
        // @formatter:on
    }
}
