package com.frank.lagomtest.preferencesquery.impl;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import com.frank.lagomtest.preferences.api.PreferencesService;
import com.frank.lagomtest.preferencesquery.api.PreferencesQueryService;
import com.frank.lagomtest.preferencesquery.api.values.AppDetails;
import com.frank.lagomtest.preferencesquery.api.values.FullAppDetails;
import com.frank.lagomtest.preferencesquery.api.values.FullAppDetails.FullBuilder;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import akka.NotUsed;

public class PreferencesQueryServiceImpl implements PreferencesQueryService {
    
	private final PreferencesService preferencesService;
	
	private final CassandraSession cassandraSession;
		
	@Inject
	public PreferencesQueryServiceImpl( PreferencesService preferencesService, 
			CassandraSession cassandraSession ) {
		super();
		this.preferencesService = preferencesService;
		this.cassandraSession = cassandraSession;
	}

	@Override
    public ServiceCall<NotUsed, PSequence<AppDetails>> getAllApps() {
        return request -> {
            CompletionStage<PSequence<AppDetails>> result =
                    cassandraSession.selectAll( "SELECT id, description, creator_id, status FROM appsummary" ).
                            thenApply( rows -> {
                                List<AppDetails> details = rows.stream().
                                        map( row -> AppDetails.builder().
                                                appId( row.getString( "id" ) ).
                                                description( row.getString( "description" ) ).
                                                creatorId( row.getString( "creator_id" ) ).
                                                status( row.getString( "status" ) ).
                                                build() ).
                                        collect( Collectors.toList() );
                                return TreePVector.from( details );
                            } );
            return result;
        };
    }

	@Override
	public ServiceCall<NotUsed, FullAppDetails> getApp( String appId ) {
		return request -> {
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
                                    status( row.getString( "status" ) ).
                                    build() );
            
            // TODO: una query con ALLOW FILTERING non è particolarmente efficiente
            // in quanto c'è il rischio di qualcosa di simile ad un full table scan
            CompletionStage<List<String>> blockIds = cassandraSession.
            		selectAll( "SELECT id from blockcontainers where app_id=? ALLOW FILTERING", appId ).
            		thenApply( rows -> 
            			rows.stream().map( row -> row.getString( "id" ) ).collect( Collectors.toList() )
            		);
            
            return detail.thenCombine( blockIds, (aDetail, ids) -> {
            		FullBuilder builder = FullAppDetails.fullBuilder().appDetails( aDetail );
            		ids.stream().forEach( id -> builder.add( FullAppDetails.BlockContainerDetail.from( id ) ) );
            		return builder.buildFull();
            });
           
        };
	}
	
	

}
