package com.frank.lagomtest.authorization.api;

import com.lightbend.lagom.javadsl.api.CircuitBreaker;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import static com.lightbend.lagom.javadsl.api.Service.*;


/**
 * @author ftorriani
 */
public interface AuthorizationService extends Service {


    ServiceCall<UserAuthorizationRequest, UserAuthorization> authorize();

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named( "authorization" ).withCalls(
                    pathCall( "/api/authorization/user", this::authorize )
                ).
                withAutoAcl( true );
        // @formatter:on
    }


}
