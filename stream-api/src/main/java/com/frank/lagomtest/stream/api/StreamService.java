/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package com.frank.lagomtest.stream.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.namedCall;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

/**
 * The stream interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the HelloStream service.
 */
public interface StreamService extends Service {

  ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> stream();

  ServiceCall<NotUsed, Source<String, NotUsed>> appEvents();

  @Override
  default Descriptor descriptor() {
    return named("stream").
            withCalls(namedCall("appEvents", this::appEvents))
      .withAutoAcl(true);
  }
}
