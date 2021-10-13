package org.justinnk.ssamason.evaluation;

import java.util.HashMap;

public class PerformanceMetrics {

	private static HashMap<String, Long> staticMetrics = new HashMap<String, Long>();

	public static void resetStatics() {
		staticMetrics.clear();
	}

	public static long getStaticMetric(String name) {
		if (staticMetrics.containsKey(name)) {
			return staticMetrics.get(name);
		} else {
			return 0l;
		}
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
		data.addEntry("timePerRun", (double) totalTime / nRuns);
		return data;
	}

	public double totalTime = 0;
	public double nRuns = 0;

	@Override
	public String toString() {
		return "Simulation took " + totalTime + "ms for " + nRuns + " replications. That is "
				+ ((float) totalTime / nRuns) + " ms per replication.";
	}
}
