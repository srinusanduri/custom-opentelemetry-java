/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent;

import com.example.javaagent.bootstrap.AgentApi;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.prometheus.client.CollectorRegistry;

/**
 * See <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/trace/sdk.md#span-processor">
 * OpenTelemetry Specification</a> for more information about {@link SpanProcessor}.
 *
 * @see DemoAutoConfigurationCustomizerProvider
 */
public class DemoSpanProcessor implements SpanProcessor {

  public static final CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;

  public DemoSpanProcessor(){
    AgentApi.doSomething(2);
    printClassLoaderHierarchy();
  }

  public void printClassLoaderHierarchy() {
    System.out.println("custom module classloader hierarchy");
    String indentation = "";
    ClassLoader classLoader = this.getClass().getClassLoader();
    while (classLoader != null) {
      System.out.println(indentation + "-->" + classLoader);
      indentation = indentation + "  ";
      classLoader = classLoader.getParent();
    }
    System.out.println(indentation + "-->" + "BootstrapClassLoader");
  }

  private io.prometheus.client.Counter totalRequests;

  @Override
  public void onStart(Context parentContext, ReadWriteSpan span) {

    //System.out.println("FROM AGENT GlobalOpenTelemetry : " + GlobalOpenTelemetry.get());

    span.setAttribute("custom", "demo");
    Baggage.fromContext(parentContext)
        .forEach((s, baggageEntry) -> span.setAttribute(s, baggageEntry.getValue()));

    if (this.totalRequests == null) {
      this.totalRequests = io.prometheus.client.Counter.build().name("traced_spans").help("Total spans").register(collectorRegistry);
      System.out.println("Span counter INITIALIZED... ");
      System.out.println("Default registry in agent");
    }

    this.totalRequests.inc();
    System.out.println("Span counter INCREMENTED... ");

    System.out.println(
        "Send parent span operation duration metric here if service or operation boundary is detected based on baggage values");
  }

  @Override
  public boolean isStartRequired() {
    return true;
  }

  @Override
  public void onEnd(ReadableSpan span) {}

  @Override
  public boolean isEndRequired() {
    return false;
  }

  @Override
  public CompletableResultCode shutdown() {
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public CompletableResultCode forceFlush() {
    return CompletableResultCode.ofSuccess();
  }
}
