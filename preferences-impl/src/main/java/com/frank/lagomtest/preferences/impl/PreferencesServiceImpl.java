package com.frank.lagomtest.preferences.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.javadsl.Source;
import com.frank.lagomtest.authorization.api.AuthorizationService;
import com.frank.lagomtest.authorization.api.Role;
import com.frank.lagomtest.authorization.api.UserAuthorizationRequest;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.frank.lagomtest.preferences.api.event.PreferencesEvent;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import com.frank.lagomtest.preferences.api.values.AllApps;
import com.frank.lagomtest.preferences.api.values.CreateAppDone;
import com.frank.lagomtest.preferences.api.values.AppDetails;
import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferences.impl.AppCommand.ActivateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.AddBlockContainer;
import com.frank.lagomtest.preferences.impl.AppCommand.CreateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.GetApp;
import com.frank.lagomtest.preferences.impl.AppCommand.RemoveBlockContainer;
import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
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
import java.util.stream.Collectors;

import static com.frank.lagomtest.authorization.api.Role.ADMIN;
import static com.frank.lagomtest.authorization.api.Role.USER;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author ftorriani
 */
public class PreferencesServiceImpl implements PreferencesService {

    private static final String SELECT_APP = "SELECT description, creator_id, status, portal_context " +
            "FROM appsummary where id = ?";

    private static final String SELECT_ALL_APPS = "SELECT id, description, portal_context, creator_id, status " +
            "FROM appsummary";

    private static final String SELECT_BLOCK_CONTAINER = "SELECT id, description from blockcontainers " +
            "where app_id=? ALLOW FILTERING";

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

    /**
     * Verifies authorization of the user identified by the token received in HTTP Header.
     * FIXME should be in a super class of the Service
     *
     * @param role        the role that should have access granted to the feature
     * @param serviceCall the serviceCall to actually perform if the user is authorized
     * @param <Request>   the request
     * @param <Response>  the response
     * @return the service call to be performed is user is authorized
     */
    private <Request, Response> ServerServiceCall<Request, Response> authorized(
            Role role,
            ServerServiceCall<Request, Response> serviceCall ) {

        return HeaderServiceCall.composeAsync( requestHeader -> {
            Optional<String> authOpt = requestHeader.getHeader( "Authorization" );
            if ( !authOpt.isPresent() ) {
                throw new Forbidden( "User not present" );
            }

            String token = authOpt.get().substring( "Bearer ".length() );

            return authorizationService.authorize().
                    invoke( UserAuthorizationRequest.from( token ) ).
                    thenApply( userAuth -> {
                        if ( userAuth.hasRole( role ) ) {
                            log.info( "User {} has role {}", userAuth.getUsername(), role );
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
    public ServiceCall<App, CreateAppDone> createApp() {

        return authorized( ADMIN, request -> entityRef( request.getAppId() ).
                ask( CreateApp.from( request ) ).
                thenApply( createAppDone -> CreateAppDone.from( request.getAppId() ) ) );
    }

    @Override
    public ServiceCall<NotUsed, AppDetails> getApp( String appId ) {

        return authorized( USER, request -> entityRef( appId ).
                ask( GetApp.build() ) );

//        return authorized( USER, request -> {
//            CompletionStage<AllApps> detail =
//                    cassandraSession.selectOne( SELECT_APP, appId ).
//                            thenApply( opt -> {
//                                if ( opt.isPresent() ) {
//                                    return opt.get();
//                                }
//                                else {
//                                    throw new NotFound( "app " + appId + " not found" );
//                                }
//                            } ).
//                            thenApply( row -> AllApps.builder().
//                                    appId( appId ).
//                                    description( row.getString( "description" ) ).
//                                    creatorId( row.getString( "creator_id" ) ).
//                                    portalContext( row.getString( "portal_context" ) ).
//                                    status( AppStatus.valueOf( row.getString( "status" ) ) ).
//                                    build() );
//
//            // FIXME: una query con ALLOW FILTERING non è particolarmente efficiente
//            // in quanto c'è il rischio di qualcosa di simile ad un full table scan
//            CompletionStage<List<BlockContainerDetail>> blockIds = cassandraSession.
//                    selectAll( SELECT_BLOCK_CONTAINER, appId ).
//                    thenApply( rows ->
//                            rows.stream().map( row -> BlockContainerDetail.builder().
//                                    blockContainerId( row.getString( "id" ) ).
//                                    description( row.getString( "description" ) ).
//                                    build() ).
//                                    collect( Collectors.toList() )
//                    );
//
//            return detail.thenCombine( blockIds, ( aDetail, bcDetails ) -> {
//                FullBuilder builder = AppDetails.fullBuilder().appDetails( aDetail );
//                bcDetails.stream().forEach( det -> builder.add( det ) );
//                return builder.buildFull();
//            } );
//
//        } );
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
        return request -> entityRef( appId ).
                ask( AppCommand.DeactivateApp.build() ).
                thenApply( r -> Done.getInstance() );
    }

    @Override
    public ServiceCall<NotUsed, Done> cancel( String appId ) {
        return request -> entityRef( appId ).
                ask( AppCommand.CancelApp.build() ).
                thenApply( r -> Done.getInstance() );
    }


    @Override
    public ServiceCall<BlockContainer, Done> addBlockContainer( String appId ) {
        return authorized( ADMIN, request -> entityRef( appId ).
                ask( AddBlockContainer.from( request ) ).
                thenApply( r -> Done.getInstance() ) );
    }

    @Override
    public ServiceCall<NotUsed, Done> removeBlockContainer( String appId, String blockContainerId ) {
        return authorized( ADMIN, request -> entityRef( appId ).
                ask( RemoveBlockContainer.from( blockContainerId ) ).
                thenApply( r -> Done.getInstance() ) );
    }

    @Override
    public ServiceCall<NotUsed, PSequence<AllApps>> getAllApps() {
        return authorized( USER, request -> {
            CompletionStage<PSequence<AllApps>> result =
                    cassandraSession.selectAll( SELECT_ALL_APPS ).
                            thenApply( rows -> {
                                List<AllApps> details = rows.stream().
                                        map( row -> AllApps.builder().
                                                appId( row.getString( "id" ) ).
                                                description( row.getString( "description" ) ).
                                                creatorId( row.getString( "creator_id" ) ).
                                                portalContext( row.getString( "portal_context" ) ).
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
                filter( evtOffset -> evtOffset.first() instanceof AppEvent ).
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
