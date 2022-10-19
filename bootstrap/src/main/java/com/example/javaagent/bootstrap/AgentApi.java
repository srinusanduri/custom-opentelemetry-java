/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.bootstrap;

import io.prometheus.client.CollectorRegistry;

/**
 * Classes in bootstrap class loader are visible for both the agent classes in agent class loader
 * and helper classes that are injected into the class loader that contains the instrumented class.
 */
public final class AgentApi {

  public static final CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;

  static {
    printClassLoaderHierarchy();
  }

  public static void doSomething(int number) {
  }

  private AgentApi() {
  }

  public static void printClassLoaderHierarchy() {
    System.out.println("Bootstrap module classloader hierarchy");
    String indentation = "";
    ClassLoader classLoader = AgentApi.class.getClassLoader();
    while (classLoader != null) {
      System.out.println(indentation + "-->" + classLoader);
      indentation = indentation + "  ";
      classLoader = classLoader.getParent();
    }
    System.out.println(indentation + "-->" + "BootstrapClassLoader");
  }
}
