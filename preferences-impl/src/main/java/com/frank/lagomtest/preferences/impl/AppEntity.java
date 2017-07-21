package com.frank.lagomtest.preferences.impl;

import akka.Done;
import com.frank.lagomtest.preferences.api.App;
import com.frank.lagomtest.preferences.api.AppStatus;
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
            b = draft( AppState.builder().
                    app( App.builder().empty().build() ).
                    build() );
        }

        return b;
    }

    private Behavior draft( AppState state ) {

        System.out.println( "===== DRAFT =====" );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addAppCreatedHandler( builder );
        addGetAppHandler( builder );

        builder.setReadOnlyCommandHandler( DeactivateApp.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( CancelApp.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( AddBlockContainer.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::unprocessed );
        builder.setCommandHandler( ActivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new AppActivated( cmd.appId ) ) );

        builder.setEventHandler( AppCreated.class,
                event -> AppState.builder().app( event.app ).status( AppStatus.DRAFT ).build() );
        builder.setEventHandlerChangingBehavior( AppActivated.class, d ->
                inactive( AppState.builder().app( state.app.get() ).status( AppStatus.ACTIVE ).build() ) );

        return builder.build();
    }

    private Behavior active( AppState state ) {

        System.out.println( "===== ACTIVE =====" );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addAppCreatedHandler( builder );
        addGetAppHandler( builder );

        builder.setCommandHandler( AddBlockContainer.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new BlockContainerAdded( cmd.blockContainerId ) ) );
        builder.setCommandHandler( RemoveBlockContainer.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new BlockContainerRemoved( cmd.blockContainerId ) ) );
        builder.setCommandHandler( DeactivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new AppDeactivated( cmd.appId ) ) );

        builder.setReadOnlyCommandHandler( ActivateApp.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( CancelApp.class, this::unprocessed );

        builder.setEventHandlerChangingBehavior( AppDeactivated.class, d ->
                inactive( AppState.builder().app( state.app.get() ).status( AppStatus.INACTIVE ).build() ) );

        return builder.build();
    }

    private Behavior inactive( AppState state ) {

        System.out.println( "===== INACTIVE =====" );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addAppCreatedHandler( builder );
        addGetAppHandler( builder );

        builder.setReadOnlyCommandHandler( AddBlockContainer.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::unprocessed );

        builder.setCommandHandler( ActivateApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new AppActivated( cmd.appId ) ) );
        builder.setCommandHandler( CancelApp.class, ( cmd, ctx ) ->
                persistAndDone( ctx, new AppCancelled( cmd.appId ) ) );

        builder.setEventHandlerChangingBehavior( AppActivated.class, d ->
                active( AppState.builder().app( state.app.get() ).status( AppStatus.ACTIVE ).build() ) );
        builder.setEventHandlerChangingBehavior( AppCancelled.class, d ->
                cancelled( AppState.builder().app( state.app.get() ).status( AppStatus.CANCELLED ).build() ) );

        return builder.build();
    }

    private Behavior cancelled( AppState state ) {
        System.out.println( "===== CANCELLED =====" );

        BehaviorBuilder builder = newBehaviorBuilder( state );
        addAppCreatedHandler( builder );
        addGetAppHandler( builder );
        builder.setReadOnlyCommandHandler( ActivateApp.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( DeactivateApp.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( AddBlockContainer.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( RemoveBlockContainer.class, this::unprocessed );
        builder.setReadOnlyCommandHandler( CancelApp.class, this::unprocessed );
        return builder.build();
    }

    private void addAppCreatedHandler( BehaviorBuilder builder ) {

        builder.setCommandHandler( CreateApp.class, ( cmd, ctx ) -> {

            if ( state().app.isPresent() && !state().app.get().isEmpty() ) {
                ctx.invalidCommand( "App " + entityId() + " is already created" );
                return ctx.done();
            }
            else {
                AppCreated appCreated = new AppCreated( entityId(), cmd.app );
                return ctx.thenPersist( appCreated, aCrt -> ctx.reply( new CreateAppDone( entityId() ) ) );
            }
        } );
    }

    private void addGetAppHandler( BehaviorBuilder builder ) {

        builder.setReadOnlyCommandHandler( GetApp.class, ( cmd, ctx ) -> {
            ctx.reply( new GetAppReply( state().app, state().status ) );
        } );
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

    private Persist<AppEvent> persistAndDone( CommandContext<Done> ctx, AppEvent event ) {
        return ctx.thenPersist( event, ( e ) -> ctx.reply( Done.getInstance() ) );
    }


}
