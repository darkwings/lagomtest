package com.frank.lagomtest.preferences.impl;

import akka.NotUsed;
import com.frank.lagomtest.preferences.api.App;
import com.frank.lagomtest.preferences.api.AppDetails;
import com.frank.lagomtest.preferences.api.CreateAppResult;
import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferences.impl.AppCommand.CreateApp;
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
                        ask( new CreateApp( request ) ).
                        thenApply( createAppDone ->
                                new CreateAppResult( ( (AppCommand.CreateAppDone) createAppDone ).getAppId() ) );
    }

    @Override
    public ServiceCall<NotUsed, AppDetails> getApp( String appId ) {
        return request -> persistentEntities.refFor( AppEntity.class, appId ).
                ask( new AppCommand.GetApp() ).
                thenApply( r -> {
                    AppCommand.GetAppReply reply = (AppCommand.GetAppReply) r;
                    if ( reply.getApp().isPresent() && !reply.getApp().get().isEmpty() ) {
                        return new AppDetails( appId, reply.getApp().get(), reply.getStatus() );
                    }
                    else {
                        throw new NotFound( "app " + appId + " not found" );
                    }
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

    /*
      Esempio di come inviare un comando ad una entity'
      @Override
      public ServiceCall<BlogCommand.AddPost, String> addPost(String id) {
        return request -> {
          PersistentEntityRef<BlogCommand> ref =
            persistentEntities.refFor(Post.class, id);
          return ref.ask(request).thenApply(ack -> "OK");
        };
      }
     */
}
