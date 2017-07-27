package com.frank.lagomtest.preferences.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.javadsl.Source;
import com.frank.lagomtest.preferences.api.event.PreferencesEvent;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import com.frank.lagomtest.preferences.api.values.CreateAppResult;
import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferences.impl.AppCommand.ActivateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.AddBlockContainer;
import com.frank.lagomtest.preferences.impl.AppCommand.CreateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.RemoveBlockContainer;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.persistence.*;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import javax.inject.Inject;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author ftorriani
 */
public class PreferencesServiceImpl implements PreferencesService {

    private final PersistentEntityRegistry persistentEntities;

    private final CassandraSession cassandraSession;

    private final ReadSide readSide;

    @Inject
    public PreferencesServiceImpl( PersistentEntityRegistry persistentEntities, 
    				CassandraSession cassandraSession, ReadSide readSide ) {
        this.persistentEntities = persistentEntities;
        this.cassandraSession = cassandraSession;
        this.readSide = readSide;

        persistentEntities.register( AppEntity.class );
        
        // Al momento spegnamo il processor locale
//        readSide.register( AppEventProcessor.class );
    }

    @Override
    public ServiceCall<NotUsed, String> echo( String id ) {
        return name -> completedFuture( "echo: " + id );
    }

	@Override
	public ServiceCall<App, CreateAppResult> createApp( String appId ) {
		return request -> entityRef( appId ).
				ask( CreateApp.from( request ) ).
				thenApply( createAppDone -> CreateAppResult.from( appId ) );
	}

	@Override
    public ServiceCall<NotUsed, Done> activate( String appId ) {
        // TODO: occorre controllare se appId esiste o no
        return request -> entityRef( appId ).
                ask( ActivateApp.build() ).
                thenApply( r -> Done.getInstance() );
    }

    @Override
    public ServiceCall<NotUsed, Done> deactivate( String appId ) {
    		throw new UnsupportedOperationException( "Unsupported" );
    	    // TODO: occorre controllare se appId esiste o no
//	    	return request -> entityRef( appId ).
//	                ask( DeactivateApp.build() ).
//	                thenApply( r -> Done.getInstance() );	
    }

    @Override
    public ServiceCall<NotUsed, Done> cancel( String appId ) {
    		throw new UnsupportedOperationException( "Unsupported" );
	    	// TODO: occorre controllare se appId esiste o no
//	    	return request -> entityRef( appId ).
//	                ask( CancelApp.build() ).
//	                thenApply( r -> Done.getInstance() );	
    }
	
    @Override
	public ServiceCall<BlockContainer, Done> addBlockContainer( String appId ) {
    		return request -> entityRef( appId ).
                ask( AddBlockContainer.from( request.blockContainerId ) ).
                thenApply( r -> Done.getInstance() );
	}
    
	@Override
	public ServiceCall<NotUsed, Done> removeBlockContainer( String appId, String blockContainerId ) {
		return request -> entityRef( appId ).
                ask( RemoveBlockContainer.from( blockContainerId ) ).
                thenApply( r -> Done.getInstance() );
	}

	

    /**
     * Permette di ottenere il riferimento ad una entity
     *
     * @param appId identificativo di una App
     * @return il riferimento ad una 'persistent entity' App
     */
    private PersistentEntityRef<AppCommand> entityRef( String appId ) {
        return persistentEntities.refFor( AppEntity.class, appId );
    }

    @Override
    public Topic<PreferencesEvent> preferencesTopic() {
        // Sharded topic
        // TopicProducer permette di pubblicare uno stream di eventi persistente,
        // la sorgente di questi eventi è il metodo streamForTag()
        return TopicProducer.taggedStreamWithOffset( AppEvent.TAG.allTags(), this::streamForTag );
    }

    private Source<Pair<PreferencesEvent, Offset>, ?> streamForTag( AggregateEventTag<AppEvent> tag, Offset offset ) {

        // Converto l'evento AppCreated in PreferencesEvent
        return persistentEntities.eventStream( tag, offset ).
                filter( evtOffset -> evtOffset.first() instanceof AppEvent ).
                mapAsync( 1, evtOffset -> {
                    AppEvent appEvent = (AppEvent) evtOffset.first();

                    System.out.println( "PreferencesServiceImpl.streamForTag: pushing AppEvent to topic: " + appEvent.getEventName() );
                    
                    return CompletableFuture.completedFuture(
                            Pair.create( PreferencesEvent.builder().
                                            appId( appEvent.getAppId() ).
                                            message( appEvent.getEventName() ).
                                            build(),
                                    evtOffset.second() ) );
                } );
    }
}
