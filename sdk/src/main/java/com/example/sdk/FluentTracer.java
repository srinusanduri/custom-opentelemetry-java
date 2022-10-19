package com.example.sdk;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;

public final class FluentTracer {

    public static Tracer getTracer(String instrumentationName) {
        Tracer tracer = GlobalOpenTelemetry.getTracer(instrumentationName + "-instrumentation");
        return tracer;
    }

}
