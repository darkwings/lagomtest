package com.frank.lagomtest.preferences.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.javadsl.Source;
import com.frank.lagomtest.authorization.api.AuthorizationService;
import com.frank.lagomtest.authorization.api.Role;
import com.frank.lagomtest.authorization.api.UserAuthorization;
import com.frank.lagomtest.authorization.api.UserAuthorizationRequest;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.frank.lagomtest.preferences.api.event.PreferencesEvent;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import com.frank.lagomtest.preferences.api.values.AppDetails;
import com.frank.lagomtest.preferences.api.values.CreateAppResult;
import com.frank.lagomtest.preferences.api.values.FullAppDetails;
import com.frank.lagomtest.preferences.api.values.FullAppDetails.BlockContainerDetail;
import com.frank.lagomtest.preferences.api.values.FullAppDetails.FullBuilder;
import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferences.impl.AppCommand.ActivateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.AddBlockContainer;
import com.frank.lagomtest.preferences.impl.AppCommand.CreateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.RemoveBlockContainer;
import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.*;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import com.lightbend.lagom.javadsl.server.ServerServiceCall;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.frank.lagomtest.authorization.api.Role.ADMIN;
import static com.frank.lagomtest.authorization.api.Role.USER;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author ftorriani
 */
public class PreferencesServiceImpl implements PreferencesService {

    private final Logger log = LoggerFactory.getLogger( PreferencesServiceImpl.class );

    private final PersistentEntityRegistry persistentEntities;

    private final CassandraSession cassandraSession;

    private final ReadSide readSide;

    private final AuthorizationService authorizationService;

    @Inject
    public PreferencesServiceImpl( PersistentEntityRegistry persistentEntities,
                                   ReadSide readSide,
                                   CassandraSession cassandraSession,
                                   AuthorizationService authorizationService ) {
        this.persistentEntities = persistentEntities;
        this.cassandraSession = cassandraSession;
        this.readSide = readSide;
        this.authorizationService = authorizationService;

        persistentEntities.register( AppEntity.class );
        readSide.register( AppEventProcessor.class );
    }

    private <Request, Response> ServerServiceCall<Request, Response> authorized(
            Role role,
            ServerServiceCall<Request, Response> serviceCall ) {
        return HeaderServiceCall.composeAsync( requestHeader -> {
            Optional<String> authOpt = requestHeader.getHeader( "Authorization" );
            if ( !authOpt.isPresent() ) {
                throw new Forbidden( "User not present" );
            }
            String auth = authOpt.get().substring( "Bearer ".length() );

            return authorizationService.authorize().
                    invoke( UserAuthorizationRequest.from( auth ) ).
                    thenApply( userAuth -> {
                        if ( userAuth.hasRole( role ) ) {
                            return serviceCall;
                        }
                        else {
                            throw new Forbidden( "User " + userAuth.getUsername() + " has no role " + role );
                        }
                    } );


        } );

    }

    @Override
    public ServiceCall<NotUsed, String> echo( String id ) {
        return name -> completedFuture( "echo: " + id );
    }

    @Override
    public ServiceCall<App, CreateAppResult> createApp( String appId ) {

        return authorized( ADMIN, request -> entityRef( appId ).
                ask( CreateApp.from( request ) ).
                thenApply( createAppDone -> CreateAppResult.from( appId ) ) );
    }

    @Override
    public ServiceCall<NotUsed, FullAppDetails> getApp( String appId ) {

        return authorized( USER, request -> {
            CompletionStage<AppDetails> detail =
                    cassandraSession.selectOne( "SELECT description, creator_id, status " +
                            "FROM appsummary where id = ?", appId ).
                            thenApply( opt -> {
                                if ( opt.isPresent() ) {
                                    return opt.get();
                                }
                                else {
                                    throw new NotFound( "app " + appId + " not found" );
                                }
                            } ).
                            thenApply( row -> AppDetails.builder().
                                    appId( appId ).
                                    description( row.getString( "description" ) ).
                                    creatorId( row.getString( "creator_id" ) ).
                                    status( AppStatus.valueOf( row.getString( "status" ) ) ).
                                    build() );

            // FIXME: una query con ALLOW FILTERING non è particolarmente efficiente
            // in quanto c'è il rischio di qualcosa di simile ad un full table scan
            CompletionStage<List<String>> blockIds = cassandraSession.
                    selectAll( "SELECT id from blockcontainers where app_id=? ALLOW FILTERING", appId ).
                    thenApply( rows ->
                            rows.stream().map( row -> row.getString( "id" ) ).collect( Collectors.toList() )
                    );

            return detail.thenCombine( blockIds, ( aDetail, ids ) -> {
                FullBuilder builder = FullAppDetails.fullBuilder().appDetails( aDetail );
                ids.stream().forEach( id -> builder.add( BlockContainerDetail.from( id ) ) );
                return builder.buildFull();
            } );

        } );
    }

    @Override
    public ServiceCall<NotUsed, Done> activate( String appId ) {
        // TODO: occorre controllare se appId esiste o no
        return authorized( ADMIN, request -> entityRef( appId ).
                ask( ActivateApp.build() ).
                thenApply( r -> Done.getInstance() ) );
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
        return authorized( ADMIN, request -> entityRef( appId ).
                ask( AddBlockContainer.from( request.blockContainerId ) ).
                thenApply( r -> Done.getInstance() ) );
    }

    @Override
    public ServiceCall<NotUsed, Done> removeBlockContainer( String appId, String blockContainerId ) {
        return authorized( ADMIN, request -> entityRef( appId ).
                ask( RemoveBlockContainer.from( blockContainerId ) ).
                thenApply( r -> Done.getInstance() ) );
    }

    @Override
    public ServiceCall<NotUsed, PSequence<AppDetails>> getAllApps() {
        return authorized( USER, request -> {
            CompletionStage<PSequence<AppDetails>> result =
                    cassandraSession.selectAll( "SELECT id, description, creator_id, status FROM appsummary" ).
                            thenApply( rows -> {
                                List<AppDetails> details = rows.stream().
                                        map( row -> AppDetails.builder().
                                                appId( row.getString( "id" ) ).
                                                description( row.getString( "description" ) ).
                                                creatorId( row.getString( "creator_id" ) ).
                                                status( AppStatus.valueOf( row.getString( "status" ) ) ).
                                                build() ).
                                        collect( Collectors.toList() );
                                return TreePVector.from( details );
                            } );
            return result;
        } );
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

        // Converto l'evento AppEvent in PreferencesEvent
        return persistentEntities.eventStream( tag, offset ).
                filter( evtOffset -> evtOffset.first() instanceof AppEvent.AppCreated ).
                mapAsync( 1, evtOffset -> {
                    AppEvent appEvent = evtOffset.first();
                    log.info( "pushing AppEvent {} to topic", appEvent );
                    return CompletableFuture.completedFuture(
                            Pair.create( PreferencesEvent.builder().
                                            appId( appEvent.getAppId() ).
                                            message( appEvent.getEventName() ).
                                            build(),
                                    evtOffset.second() ) );
                } );
    }
}
