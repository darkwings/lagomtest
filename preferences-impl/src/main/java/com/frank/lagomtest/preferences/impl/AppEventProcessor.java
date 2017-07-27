package com.frank.lagomtest.preferences.impl;

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

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatements;

/**
 * Al momento è spento
 * 
 * @author ftorriani
 */
public class AppEventProcessor extends ReadSideProcessor<AppEvent> {

    public static final String CREATE_APPSUMMARY = "CREATE TABLE IF NOT EXISTS appsummary ( " +
            "id TEXT, description TEXT, creator_id TEXT, status TEXT, PRIMARY KEY (id))";

    public static final String CREATE_BLOCKCONTAINERS = "CREATE TABLE IF NOT EXISTS blockcontainers ( " +
            "id TEXT, app_id TEXT, PRIMARY KEY (id))";

    public static final String INSERT_INTO_APPSUMMARY = "INSERT INTO appsummary " +
            "(id, description, creator_id, status) " +
            "VALUES (?, ?, ?, ?)";

    public static final String UPDATE_APPSUMMARY = "UPDATE appsummary set status=? where id=?";

    public static final String INSERT_INTO_BLOCKCONTAINERS = "INSERT INTO blockcontainers (id, app_id) " +
            "VALUES (?, ?)";

    public static final String DELETE_FROM_BLOCKCONTAINERS = "DELETE FROM blockcontainers where id = ?";

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
        BoundStatement bindWriteApp = writeApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "description", event.app.getDescription() );
        bindWriteApp.setString( "creator_id", event.app.getCreatorId() );
        bindWriteApp.setString( "status", AppStatus.DRAFT.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processAppActivated( AppEvent.AppActivated event ) {
    		System.out.println( "AppEventProcessor.processAppActivated -> " + event );
        BoundStatement bindWriteApp = updateStatusApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "status", AppStatus.ACTIVE.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processAppDeactivated( AppEvent.AppDeactivated event ) {
    	    System.out.println( "AppEventProcessor.processAppDeactivated -> " + event );
        BoundStatement bindWriteApp = updateStatusApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "status", AppStatus.INACTIVE.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processAppCancelled( AppEvent.AppCancelled event ) {
       	System.out.println( "AppEventProcessor.processAppCancelled -> " + event );
        BoundStatement bindWriteApp = updateStatusApp.bind();
        bindWriteApp.setString( "id", event.appId );
        bindWriteApp.setString( "status", AppStatus.CANCELLED.name() );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processBlockContainerAdded( AppEvent.BlockContainerAdded event ) {
      	System.out.println( "AppEventProcessor.processBlockContainerAdded -> " + event );
        BoundStatement bindWriteApp = writeBlockContainer.bind();
        bindWriteApp.setString( "id", event.blockContainerId );
        bindWriteApp.setString( "app_id", event.appId );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    private CompletionStage<List<BoundStatement>> processBlockContainerRemoved( AppEvent.BlockContainerRemoved event ) {
    	    System.out.println( "AppEventProcessor.processBlockContainerRemoved -> " + event );
        BoundStatement bindWriteApp = deleteBlockContainer.bind();
        bindWriteApp.setString( "id", event.blockContainerId );
        return completedStatements( Arrays.asList( bindWriteApp ) );
    }

    @Override
    public PSequence<AggregateEventTag<AppEvent>> aggregateTags() {
        return AppEvent.TAG.allTags();
    }
}
