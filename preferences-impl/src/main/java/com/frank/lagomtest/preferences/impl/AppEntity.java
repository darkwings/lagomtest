package com.frank.lagomtest.preferences.impl;

import akka.Done;
import com.frank.lagomtest.preferences.api.App;
import com.frank.lagomtest.preferences.impl.AppCommand.*;
import com.frank.lagomtest.preferences.impl.AppEvent.*;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

/**
 * The App entity
 *
 * @author ftorriani
 */
public class AppEntity extends PersistentEntity<AppCommand, AppEvent, AppState> {

    @Override
    public Behavior initialBehavior( Optional<AppState> snapshot ) {
        Behavior b;
        if ( snapshot.isPresent() ) {
            AppState state = snapshot.get();
            switch ( state.status ) {
                case NOT_STARTED:
                    b = notStarted( state );
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
            b = notStarted( AppState.builder().
                    app( App.builder().
                            creatorId( "" ).
                            uniqueId( "" ).
                            build() ).
                    build() );
        }

        return b;
    }

    private Behavior notStarted( AppState state ) {

        System.out.println( "===== NOT STARTED =====" );

        BehaviorBuilder b = newBehaviorBuilder( state );

        b.setReadOnlyCommandHandler( DeactivateApp.class, this::unprocessed );
        b.setReadOnlyCommandHandler( CancelApp.class, this::unprocessed );
        b.setReadOnlyCommandHandler( AddBlockContainer.class, this::unprocessed );
        b.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::unprocessed );

        b.setCommandHandler( CreateApp.class, ( start, ctx ) -> {
            AppCreated appCreated = new AppCreated( entityId(), start.app );
            return ctx.thenPersist( appCreated, aCrt -> ctx.reply( new CreateAppDone( entityId() ) ) );
        } );

        b.setEventHandler( AppCreated.class,
                event -> AppState.start( event.app ) );

        return b.build();
    }

    private Behavior active( AppState state ) {

        System.out.println( "===== ACTIVE =====" );

        BehaviorBuilder b = newBehaviorBuilder( state );

        b.setCommandHandler( AddBlockContainer.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new BlockContainerAdded( cmd.blockContainerId ) ) );
        b.setCommandHandler( RemoveBlockContainer.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new BlockContainerRemoved( cmd.blockContainerId ) ) );
        b.setCommandHandler( DeactivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new AppDeactivated( cmd.appId ) ) );

        b.setReadOnlyCommandHandler( CreateApp.class, this::unprocessedCreate );
        b.setReadOnlyCommandHandler( ActivateApp.class, this::unprocessed );
        b.setReadOnlyCommandHandler( CancelApp.class, this::unprocessed );

        b.setEventHandler( AppDeactivated.class,
                event -> AppState.builder().app( state.app.get() ).status( AppStatus.INACTIVE ).build() );

        return b.build();
    }

    private Behavior inactive( AppState state ) {

        System.out.println( "===== INACTIVE =====" );

        BehaviorBuilder b = newBehaviorBuilder( state );

        b.setReadOnlyCommandHandler( CreateApp.class, this::unprocessedCreate );
        b.setReadOnlyCommandHandler( AddBlockContainer.class, this::unprocessed );
        b.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::unprocessed );

        b.setCommandHandler( ActivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new AppActivated( cmd.appId ) ) );
        b.setCommandHandler( CancelApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new AppEvent.AppCancelled( cmd.appId ) ) );

        b.setEventHandler( AppActivated.class,
                event -> AppState.builder().app( state.app.get() ).status( AppStatus.INACTIVE ).build() );
        b.setEventHandlerChangingBehavior( AppCancelled.class, cancel ->
                cancelled( state().withStatus( AppStatus.CANCELLED ) )
        );

        return b.build();
    }

    private Behavior cancelled( AppState state ) {
        System.out.println( "===== CANCELLED =====" );

        BehaviorBuilder b = newBehaviorBuilder( state );
        b.setReadOnlyCommandHandler( CreateApp.class, this::unprocessedCreate );
        b.setReadOnlyCommandHandler( ActivateApp.class, this::unprocessed );
        b.setReadOnlyCommandHandler( DeactivateApp.class, this::unprocessed );
        b.setReadOnlyCommandHandler( AddBlockContainer.class, this::unprocessed );
        b.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::unprocessed );
        b.setReadOnlyCommandHandler( CancelApp.class, this::unprocessed );
        return b.build();
    }

    /**
     * Does nothing and replies with {@link Done}
     *
     * @param command the command
     * @param ctx     the context
     */
    private void unprocessed( Object command, ReadOnlyCommandContext<Done> ctx ) {
        ctx.reply( Done.getInstance() );
    }

    /**
     * Does nothing and replies with {@link Done}
     *
     * @param command the command
     * @param ctx     the context
     */
    private void unprocessedCreate( Object command, ReadOnlyCommandContext<CreateAppDone> ctx ) {
        ctx.invalidCommand( "Invalid command" );
        ctx.reply( new CreateAppDone( "invalid" ) );
    }

    private Persist<AppEvent> persistAndDone( CommandContext<Done> ctx, AppEvent event ) {
        return ctx.thenPersist( event, ( e ) -> ctx.reply( Done.getInstance() ) );
    }


}
