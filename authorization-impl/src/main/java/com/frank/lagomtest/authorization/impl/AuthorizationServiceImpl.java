package com.frank.lagomtest.authorization.impl;

import com.frank.lagomtest.authorization.api.AuthorizationService;
import com.frank.lagomtest.authorization.api.UserAuthorization;
import com.frank.lagomtest.authorization.api.UserAuthorizationRequest;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.frank.lagomtest.authorization.api.Role.ADMIN;
import static com.frank.lagomtest.authorization.api.Role.USER;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author ftorriani
 */
public class AuthorizationServiceImpl implements AuthorizationService {

    private final Logger log = LoggerFactory.getLogger( AuthorizationServiceImpl.class );

    @Override
    public ServiceCall<UserAuthorizationRequest, UserAuthorization> authorize() {
        return request -> {

            // TODO parsing token JWT
            String token = request.getToken();

            log.info( "Authorization for token {}", token );

            if ( "1111222233334444".equals( token ) ) {
                return completedFuture( UserAuthorization.builder().
                        username( "_admin" ).
                        role( ADMIN ).
                        role( USER ).
                        build() );
            }
            else if ( "4444333322221111".equals( token ) ) {
                return completedFuture( UserAuthorization.builder().
                        username( "_user" ).
                        role( USER ).
                        build() );
            }
            else {
                throw new Forbidden( "Access forbidden" );
            }
        };
    }
}
