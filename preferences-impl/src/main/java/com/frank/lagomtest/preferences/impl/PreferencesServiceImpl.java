package com.frank.lagomtest.preferences.impl;

import akka.NotUsed;
import com.frank.lagomtest.preferences.api.App;
import com.frank.lagomtest.preferences.api.AppResult;
import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferences.impl.AppCommand.CreateApp;
import com.lightbend.lagom.javadsl.api.ServiceCall;
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
    public ServiceCall<App, AppResult> newApp( String appId ) {
        return app ->
                persistentEntities.refFor( AppEntity.class, appId ).
                    ask( new CreateApp( app ) ).
                    thenApply( createAppDone ->
                            new AppResult( ( (AppCommand.CreateAppDone) createAppDone ).getAppId() ) );
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
