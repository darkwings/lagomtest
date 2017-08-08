package com.frank.lagomtest.preferences.impl.app;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.frank.lagomtest.preferences.api.AppStatus;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatements;

/**
 * Read-side processor for Cassandra. It updates the database based on {@link AppEvent events} generated
 * by the persistent {@link AppEntity}
 *
 * @author ftorriani
 */
public class AppEventProcessor extends ReadSideProcessor<AppEvent> {

    private final Logger log = LoggerFactory.getLogger( AppEventProcessor.class );

//    private static final AtomicInteger COUNTER = new AtomicInteger( 0 );

    private static final String CREATE_APPSUMMARY = "CREATE TABLE IF NOT EXISTS appsummary ( " +
            "id TEXT, description TEXT, creator_id TEXT, status TEXT, PRIMARY KEY (id))";

    private static final String CREATE_BLOCKCONTAINERS = "CREATE TABLE IF NOT EXISTS blockcontainers ( " +
            "id TEXT, app_id TEXT, PRIMARY KEY (id))";

    private static final String INSERT_INTO_APPSUMMARY = "INSERT INTO appsummary " +
            "(id, description, creator_id, status) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_APPSUMMARY = "UPDATE appsummary set status=? where id=?";

    private static final String INSERT_INTO_BLOCKCONTAINERS = "INSERT INTO blockcontainers (id, app_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_FROM_BLOCKCONTAINERS = "DELETE FROM blockcontainers where id = ?";

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeApp = null;
    private PreparedStatement updateStatusApp = null;
    private PreparedStatement writeBlockContainer = null;
    private PreparedStatement deleteBlockContainer = null;


    @Inject
    public AppEventProcessor( CassandraSession session, CassandraReadSide readSide ) {
        this.session = session;
        this.readSide = readSide;
    }

    @Override
    public ReadSideHandler<AppEvent> buildHandler() {
        CassandraReadSide.ReadSideHandlerBuilder<AppEvent> builder =
                readSide.builder( "appoffset" );
        builder.setGlobalPrepare( this::createTable );
        builder.setPrepare( tag -> prepareWriteApp() );
        builder.setEventHandler( AppEvent.AppCreated.class, this::processAppCreated );
        builder.setEventHandler( AppEvent.AppActivated.class, this::processAppActivated );
        builder.setEventHandler( AppEvent.AppDeactivated.class, this::processAppDeactivated );
        builder.setEventHandler( AppEvent.AppCancelled.class, this::processAppCancelled );
        builder.setEventHandler( AppEvent.BlockContainerAdded.class, this::processBlockContainerAdded );
        builder.setEventHandler( AppEvent.BlockContainerRemoved.class, this::processBlockContainerRemoved );
        return builder.build();
    }

    private CompletionStage<Done> createTable() {
        return session.
                executeCreateTable( CREATE_APPSUMMARY ).
                thenCompose( d -> session.executeCreateTable( CREATE_BLOCKCONTAINERS ) );
    }

    private CompletionStage<Done> prepareWriteApp() {
        return session.prepare( INSERT_INTO_APPSUMMARY )
                .thenApply( ps -> {
                    this.writeApp = ps;
                    return Done.getInstance();
                } ).
                        thenCompose( d -> session.prepare( UPDATE_APPSUMMARY ) ).
                        thenApply( ps -> {
                            this.updateStatusApp = ps;
                            return Done.getInstance();
                        } ).
                        thenCompose( d -> session.prepare( INSERT_INTO_BLOCKCONTAINERS ) ).
                        thenApply( ps -> {
                            this.writeBlockContainer = ps;
                            return Done.getInstance();
                        } ).
                        thenCompose( d -> session.prepare( DELETE_FROM_BLOCKCONTAINERS ) ).
                        thenApply( ps -> {
                            this.deleteBlockContainer = ps;
                            return Done.getInstance();
                        } )
                ;
    }

    private CompletionStage<List<BoundStatement>> processAppCreated( AppEvent.AppCreated event ) {

        // Simuliamo un fallimento temporaneo del servizio
//        int i = COUNTER.incrementAndGet();
//        if ( i < 2 ) {
//            throw new RuntimeException( "Catch this!!!" );
//        }
        log.info( "processAppCreated -> {}", event );

        BoundStatement bindWriteApp = writeApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "description", event.app.getDescription() );
        bindWriteApp.setString( "creator_id", event.app.getCreatorId() );
        bindWriteApp.setString( "status", AppStatus.DRAFT.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processAppActivated( AppEvent.AppActivated event ) {

        log.info( "processAppActivated -> {}", event );
        BoundStatement bindWriteApp = updateStatusApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "status", AppStatus.ACTIVE.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processAppDeactivated( AppEvent.AppDeactivated event ) {
        log.info( "processAppDeactivated -> {}", event );
        BoundStatement bindWriteApp = updateStatusApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "status", AppStatus.INACTIVE.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processAppCancelled( AppEvent.AppCancelled event ) {
        log.info( "processAppCancelled -> {}", event );
        BoundStatement bindWriteApp = updateStatusApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "status", AppStatus.CANCELLED.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processBlockContainerAdded( AppEvent.BlockContainerAdded event ) {
        log.info( "processBlockContainerAdded -> {}", event );
        BoundStatement bindWriteApp = writeBlockContainer.bind();
        bindWriteApp.setString( "id", event.blockContainerId );
        bindWriteApp.setString( "app_id", event.appId );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processBlockContainerRemoved( AppEvent.BlockContainerRemoved event ) {
        log.info( "processBlockContainerRemoved -> ", event );
        BoundStatement bindWriteApp = deleteBlockContainer.bind();
        bindWriteApp.setString( "id", event.blockContainerId );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    @Override
    public PSequence<AggregateEventTag<AppEvent>> aggregateTags() {
        return AppEvent.TAG.allTags();
    }
}
