package com.frank.lagomtest.authorization.impl;

import com.frank.lagomtest.authorization.api.AuthorizationService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * @author ftorriani
 */
public class AuthorizationModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {

        bindService( AuthorizationService.class, AuthorizationServiceImpl.class );

    }
}
