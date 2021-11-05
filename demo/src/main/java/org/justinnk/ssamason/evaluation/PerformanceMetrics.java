/*
 * MIT License
 *
 * Copyright (c) 2021 Justin Kreikemeyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.justinnk.ssamason.evaluation;

import java.util.HashMap;

public class PerformanceMetrics {

  private static HashMap<String, Long> staticMetrics = new HashMap<>();

  public static void resetStatics() {
    staticMetrics.clear();
  }

  public static long getStaticMetric(String name) {
    return staticMetrics.getOrDefault(name, 0L);
  }

  public static void printStaticMetric(String name) {
    System.out.println(name + ": " + getStaticMetric(name));
  }

  public static void printAllStaticMetrics() {
    for (Object key : staticMetrics.keySet().stream().sorted().toArray()) {
      System.out.println(key + ": " + staticMetrics.get(key));
    }
  }

  public static void setStaticMetric(String name, long value) {
    staticMetrics.put(name, value);
  }

  public static void incStaticMetric(String name) {
    staticMetrics.put(name, getStaticMetric(name) + 1);
  }

  public static void incStaticMetric(String name, long value) {
    staticMetrics.put(name, getStaticMetric(name) + value);
  }

  public DataFrame toDataFrame() {
    DataFrame data = new DataFrame();
    for (String metric : staticMetrics.keySet()) {
      data.addEntry(metric, staticMetrics.get(metric));
    }
    data.addEntry("totalTime", totalTime);
    data.addEntry("nRuns", nRuns);
    data.addEntry("timePerRun", totalTime / nRuns);
    return data;
  }

  public double totalTime = 0;
  public double nRuns = 0;

  @Override
  public String toString() {
    return "Simulation took "
        + totalTime
        + "ms for "
        + nRuns
        + " replications. That is "
        + ((float) totalTime / nRuns)
        + " ms per replication.";
  }
}
