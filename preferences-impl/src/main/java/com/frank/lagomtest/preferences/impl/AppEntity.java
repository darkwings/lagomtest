package com.frank.lagomtest.preferences.impl;

import akka.Done;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.frank.lagomtest.preferences.impl.AppCommand.*;
import com.frank.lagomtest.preferences.impl.AppEvent.*;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.frank.lagomtest.preferences.api.AppStatus.ACTIVE;
import static com.frank.lagomtest.preferences.api.AppStatus.CANCELLED;

/**
 * The App entity
 *
 * @author ftorriani
 */
public class AppEntity extends PersistentEntity<AppCommand, AppEvent, AppState> {

    private final Logger log = LoggerFactory.getLogger( AppEntity.class );

    @Override
    public Behavior initialBehavior( Optional<AppState> snapshot ) {
        Behavior b;
        if ( snapshot.isPresent() ) {
            log.info( "AppEntity.initialBehavior: Snapshot is present" );
            AppState state = snapshot.get();
            switch ( state.status ) {
                case DRAFT:
                    b = draft( state );
                    break;
                case ACTIVE:
                    b = active( state );
                    break;
                case INACTIVE:
                    b = inactive( state );
                    break;
                case CANCELLED:
                    b = cancelled( state );
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        else {
            log.info( "AppEntity.initialBehavior: Snapshot is NOT present" );
            b = draft( AppState.builder().
                    app( App.empty() ).
                    build() );
        }

        return b;
    }

    private Behavior draft( AppState state ) {

        log.info( "AppEntity {}: ===== DRAFT =====", entityId() );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addCreateAppCommandHandler( builder );
        addGetAppCommandHandler( builder );
        builder.setCommandHandler( ActivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, AppActivated.from( entityId() ) ) );

        builder.setReadOnlyCommandHandler( DeactivateApp.class, this::notValid );
        builder.setReadOnlyCommandHandler( CancelApp.class, this::notValid );
        builder.setReadOnlyCommandHandler( AddBlockContainer.class, this::notValid );
        builder.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::notValid );


        builder.setEventHandler( AppCreated.class,
                event -> AppState.builder().
                        app( event.app ).
                        status( AppStatus.DRAFT ).
                        build() );

        builder.setEventHandlerChangingBehavior( AppActivated.class, event ->
                active( AppState.builder( state() ).
                        status( ACTIVE ).
                        build() ) );

        return builder.build();
    }

    private Behavior active( AppState state ) {

        log.info( "AppEntity {}: ===== ACTIVE =====", entityId() );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addCreateAppCommandHandler( builder );
        addGetAppCommandHandler( builder );

        builder.setCommandHandler( AddBlockContainer.class, ( cmd, ctx ) ->
                persistAndDone( ctx, BlockContainerAdded.builder().
                        appId( entityId() ).
                        blockContainerId( cmd.blockContainerId ).
                        build() ) );
        builder.setCommandHandler( RemoveBlockContainer.class, ( cmd, ctx ) ->
                persistAndDone( ctx, BlockContainerRemoved.builder().
                        appId( entityId() ).
                        blockContainerId( cmd.blockContainerId ).
                        build() ) );

        builder.setCommandHandler( DeactivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, AppDeactivated.from( entityId() ) ) );

        builder.setReadOnlyCommandHandler( ActivateApp.class, this::notValid );
        builder.setReadOnlyCommandHandler( CancelApp.class, this::notValid );

        builder.setEventHandlerChangingBehavior( AppDeactivated.class, d ->
                inactive( AppState.builder( state() ).
                        status( AppStatus.INACTIVE ).
                        build() ) );

        return builder.build();
    }

    private Behavior inactive( AppState state ) {

        log.info( "AppEntity {}: ===== INACTIVE =====", entityId() );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addCreateAppCommandHandler( builder );
        addGetAppCommandHandler( builder );

        builder.setReadOnlyCommandHandler( AddBlockContainer.class, this::notValid );
        builder.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::notValid );

        builder.setCommandHandler( ActivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, AppActivated.from( entityId() ) ) );
        builder.setCommandHandler( CancelApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, AppCancelled.from( entityId() ) ) );

        builder.setEventHandlerChangingBehavior( AppActivated.class, d ->
                active( AppState.builder( state() ).
                        status( ACTIVE ).
                        build() ) );
        builder.setEventHandlerChangingBehavior( AppCancelled.class, d ->
                cancelled( AppState.builder( state() ).
                        status( CANCELLED ).
                        build() ) );

        return builder.build();
    }

    private Behavior cancelled( AppState state ) {
        log.info( "AppEntity {}: ===== CANCELLED =====", entityId() );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addCreateAppCommandHandler( builder );
        addGetAppCommandHandler( builder );

        builder.setReadOnlyCommandHandler( ActivateApp.class, this::notValid );
        builder.setReadOnlyCommandHandler( DeactivateApp.class, this::notValid );
        builder.setReadOnlyCommandHandler( AddBlockContainer.class, this::notValid );
        builder.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::notValid );
        builder.setReadOnlyCommandHandler( CancelApp.class, this::notValid );
        return builder.build();
    }

    private void addCreateAppCommandHandler( BehaviorBuilder builder ) {


        builder.setCommandHandler( CreateApp.class, ( cmd, ctx ) -> {

            if ( state().app.isPresent() && !state().app.get().isEmpty() ) {
                ctx.invalidCommand( "App " + entityId() + " cannot be created" );
                return ctx.done();
            }
            else {
                return ctx.thenPersist( AppCreated.builder().
                                appId( entityId() ).
                                app( cmd.app ).
                                build(),
                        aCrt -> ctx.reply( CreateAppDone.from( entityId() ) ) );
            }
        } );
    }

    private void addGetAppCommandHandler( BehaviorBuilder builder ) {

        builder.setReadOnlyCommandHandler( GetApp.class, ( cmd, ctx ) -> {
            ctx.reply( GetAppReply.builder().
                    app( state().app ).
                    status( state().status ).
                    build() );
        } );
    }


    /**
     * Does nothing and replies with {@link Done}
     *
     * @param command the command
     * @param ctx     the context
     */
    private void notValid( Object command, ReadOnlyCommandContext<Done> ctx ) {
        ctx.invalidCommand( "Command " + command + " is invalid in the current state of app " +
                entityId() + " (" + state().status + ")" );
        ctx.reply( Done.getInstance() );
    }

    private Persist<AppEvent> persistAndDone( CommandContext<Done> ctx, AppEvent event ) {
        return ctx.thenPersist( event, ( e ) -> ctx.reply( Done.getInstance() ) );
    }


}
