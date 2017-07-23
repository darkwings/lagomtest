package com.frank.lagomtest.preferences.impl;

import akka.Done;
import akka.NotUsed;
import com.frank.lagomtest.preferences.api.App;
import com.frank.lagomtest.preferences.api.AppDetails;
import com.frank.lagomtest.preferences.api.CreateAppResult;
import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferences.impl.AppCommand.ActivateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.CreateApp;
import com.frank.lagomtest.preferences.impl.AppCommand.GetApp;
import com.frank.lagomtest.preferences.impl.AppCommand.GetAppReply;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author ftorriani
 */
public class PreferencesServiceImpl implements PreferencesService {

    private final PersistentEntityRegistry persistentEntities;

    @Inject
    public PreferencesServiceImpl( PersistentEntityRegistry persistentEntities ) {
        this.persistentEntities = persistentEntities;
        persistentEntities.register( AppEntity.class );
    }

    @Override
    public ServiceCall<NotUsed, String> echo( String id ) {
        return name -> completedFuture( "echo: " + id );
    }

    @Override
    public ServiceCall<App, CreateAppResult> createApp( String appId ) {
        return request ->
                persistentEntities.refFor( AppEntity.class, appId ).
                        ask( CreateApp.from( request ) ).
                        thenApply( createAppDone ->
                                CreateAppResult.from( appId ) );
    }

    @Override
    public ServiceCall<NotUsed, AppDetails> getApp( String appId ) {
        return request -> entityRef( appId ).
                ask( GetApp.build() ).
                thenApply( r -> {
                    GetAppReply reply = (GetAppReply) r;
                    if ( reply.getApp().isPresent() && !reply.getApp().get().isEmpty() ) {

                        return AppDetails.builder().
                                appId( appId ).
                                app (reply.getApp().get() ).
                                status( reply.getStatus() ).
                                build();
                    }
                    else {
                        throw new NotFound( "app " + appId + " not found" );
                    }
                } );
    }

    @Override
    public ServiceCall<NotUsed, Done> activate( String appId ) {
        // TODO: occorre controllare se appId esiste o no
        return request -> entityRef( appId ).
                ask( ActivateApp.build() ).
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
}
