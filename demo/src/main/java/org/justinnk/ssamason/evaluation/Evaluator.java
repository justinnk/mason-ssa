/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.evaluation;

import org.justinnk.ssamason.evaluation.benchmarks.*;
import org.justinnk.ssamason.evaluation.correctness.PredatorPreyCorrectnessExperiment;
import org.justinnk.ssamason.evaluation.correctness.SSASirCorrectnessExperiment;
import org.justinnk.ssamason.evaluation.correctness.SSASirsCorrectnessExperiment;
import org.justinnk.ssamason.evaluation.correctness.VanillaSirsCorrectnessExperiment;
import org.justinnk.ssamason.extension.Agent;
import org.justinnk.ssamason.extension.ssa.FirstReactionMethod;
import org.justinnk.ssamason.extension.ssa.LogarithmicDirectMethod;
import org.justinnk.ssamason.extension.ssa.NextReactionMethod;
import org.justinnk.ssamason.extension.ssa.OptimizedDirectMethod;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Program to execute all correctness and performance tests to reproduce the results provided in the
 * thesis.
 *
 * <p>Provides a simple CLI, so that testing can be limited to certain simulators. This can be used
 * in conjunction with Maven profiles to allow disabling or enabling (possibly) performance-critical
 * aspects.
 */
public class Evaluator {

  private enum Method {
    vanilla,
    frm,
    ldm,
    nrm,
    odm,
    frm_overhead
  }

  private static int numIterations = 20;
  private static int warmupIterations = 10;
  private static int warmupTime = 1;
  private static int measurementTime = 2;
  private static int numForks = 3;

  public static void main(String[] args) throws RunnerException {
    /* Gather methods to test from CLI-arguments */
    Method method;
    if (args.length != 1) {
      System.out.println("Usage: <method>, <method> ::= frm | ldm | nrm | odm | vanilla");
      System.exit(1);
    }
    method = Method.valueOf(args[0]);
    /* Deactivate logging for agents */
    Agent.enableAgentLogging = false;
    /* Wait for possible profiler */
    /*
     * System.out.println("Press any key to start..."); try { System.in.read(); }
     * catch (IOException e) { e.printStackTrace(); }
     */
    /* Execute evaluation */
    //		sirsCorrectness(method);
    //		sirCorrectness(method);
    //		predatorPreyCorrectnes(method);
    gridPerformance(method);
    initPerformance(method);
    overhead(method);
    erdosRenyiPerformance(method);
    // sheepPerformance(method);
  }

  /**
   * Simulate the model with each simulator on the SIRS model(s) and store the means of S,I and R
   * population counts over time.
   */
  private static void sirsCorrectness(Method method) {
    /* TODO: test multiple parameter configurations. */
    int numHumans = 100;
    double density = 0.1;
    int initialInfected = 1;
    int reps = 800;
    switch (method) {
      case frm:
        new SSASirsCorrectnessExperiment(
                "FirstReactionMethod",
                new FirstReactionMethod(),
                numHumans,
                density,
                initialInfected)
            .run(reps);
        break;
      case ldm:
        new SSASirsCorrectnessExperiment(
                "LogarithmicDirectMethod",
                new LogarithmicDirectMethod(),
                numHumans,
                density,
                initialInfected)
            .run(reps);
        break;
      case nrm:
        new SSASirsCorrectnessExperiment(
                "NextReactionMethod", new NextReactionMethod(), numHumans, density, initialInfected)
            .run(reps);
        break;
      case odm:
        new SSASirsCorrectnessExperiment(
                "OptimizedDirectMethod",
                new OptimizedDirectMethod(),
                numHumans,
                density,
                initialInfected)
            .run(reps);
        break;
      case vanilla:
        new VanillaSirsCorrectnessExperiment(numHumans, density, initialInfected).run(reps);
        break;
      default:
        break;
    }
  }

  /**
   * Simulate the model with each simulator and store the means of S,I and R population counts over
   * time.
   */
  private static void sirCorrectness(Method method) {
    /* TODO: test multiple parameter configurations. */
    int numHumans = 100;
    double density = 0.1;
    int initialInfected = 1;
    int reps = 2000;
    switch (method) {
      case frm:
        new SSASirCorrectnessExperiment(
                "FirstReactionMethod",
                new FirstReactionMethod(),
                numHumans,
                density,
                initialInfected)
            .run(reps);
        break;
      case ldm:
        new SSASirCorrectnessExperiment(
                "LogarithmicDirectMethod",
                new LogarithmicDirectMethod(),
                numHumans,
                density,
                initialInfected)
            .run(reps);
        break;
      case nrm:
        new SSASirCorrectnessExperiment(
                "NextReactionMethod", new NextReactionMethod(), numHumans, density, initialInfected)
            .run(reps);
        break;
      case odm:
        new SSASirCorrectnessExperiment(
                "OptimizedDirectMethod",
                new OptimizedDirectMethod(),
                numHumans,
                density,
                initialInfected)
            .run(reps);
        break;
      default:
        break;
    }
  }

  private static void predatorPreyCorrectnes(Method method) {
    /* TODO: test multiple parameter configurations. */
    int numWolf = 20; // 15;
    int numSheep = 10;
    int reps = 800;
    switch (method) {
      case frm:
        new PredatorPreyCorrectnessExperiment(
                "FirstReactionMethod", new FirstReactionMethod(), numWolf, numSheep)
            .run(reps);
        break;
      case ldm:
        new PredatorPreyCorrectnessExperiment(
                "LogarithmicDirectMethod", new LogarithmicDirectMethod(), numWolf, numSheep)
            .run(reps);
        break;
      case nrm:
        new PredatorPreyCorrectnessExperiment(
                "NextReactionMethod", new NextReactionMethod(), numWolf, numSheep)
            .run(reps);
        break;
      case odm:
        new PredatorPreyCorrectnessExperiment(
                "OptimizedDirectMethod", new OptimizedDirectMethod(), numWolf, numSheep)
            .run(reps);
        break;
      default:
        break;
    }
  }

  /**
   * Test the performance of different simulators on a grid-like contact graph.
   *
   * @throws RunnerException When jmh fails.
   */
  private static void gridPerformance(Method method) throws RunnerException {
    OptionsBuilder opt = new OptionsBuilder();
    switch (method) {
      case frm:
        opt.include(BenchmarkSirsGridFrm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/FirstReactionMethod_grid_jmh.csv");
        break;
      case ldm:
        opt.include(BenchmarkSirsGridLdm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/LogarithmicDirectMethod_grid_jmh.csv");
        break;
      case nrm:
        opt.include(BenchmarkSirsGridNrm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/NextReactionMethod_grid_jmh.csv");
        break;
      case odm:
        opt.include(BenchmarkSirsGridOdm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/OptimizedDirectMethod_grid_jmh.csv");
        break;
      case vanilla:
        opt.include(BenchmarkSirsGridVanilla.class.getSimpleName())
            .result("evaluation/performance_data/sirs/Vanilla_grid_jmh.csv");
        break;
      default:
        break;
    }
    opt.measurementIterations(numIterations)
        .warmupIterations(warmupIterations)
        .warmupTime(TimeValue.seconds(warmupTime))
        .measurementTime(TimeValue.seconds(measurementTime))
        .forks(numForks)
        .jvmArgs("-Xms2g", "-Xmx7g")
        .resultFormat(ResultFormatType.CSV)
        .build();
    new Runner(opt).run();
  }

  /**
   * Test the performance of different simulators depending on total population size and contact
   * graph density.
   *
   * @throws RunnerException When jmh fails.
   */
  private static void erdosRenyiPerformance(Method method) throws RunnerException {
    OptionsBuilder opt = new OptionsBuilder();
    switch (method) {
      case frm:
        opt.include(BenchmarkSirsErdosRenyiFrm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/FirstReactionMethod_erdos_jmh.csv");
        break;
      case ldm:
        opt.include(BenchmarkSirsErdosRenyiLdm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/LogarithmicDirectMethod_erdos_jmh.csv");
        break;
      case nrm:
        opt.include(BenchmarkSirsErdosRenyiNrm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/NextReactionMethod_erdos_jmh.csv");
        break;
      case odm:
        opt.include(BenchmarkSirsErdosRenyiOdm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/OptimizedDirectMethod_erdos_jmh.csv");
        break;
      case vanilla:
        opt.include(BenchmarkSirsErdosRenyiVanilla.class.getSimpleName())
            .result("evaluation/performance_data/sirs/Vanilla_erdos_jmh.csv");
        break;
      default:
        break;
    }
    opt.measurementIterations(numIterations)
        .warmupIterations(warmupIterations)
        .warmupTime(TimeValue.seconds(warmupTime))
        .measurementTime(TimeValue.seconds(measurementTime))
        .forks(numForks)
        .jvmArgs("-Xms2g", "-Xmx7g")
        .resultFormat(ResultFormatType.CSV)
        .build();
    new Runner(opt).run();
  }

  /**
   * Test the performance of the FRM with the NRM aspect woven into the model.
   *
   * @throws RunnerException When jmh fails.
   */
  private static void overhead(Method method) throws RunnerException {
    if (method == Method.frm_overhead) {
      OptionsBuilder opt = new OptionsBuilder();
      opt.include(BenchmarkSirsGridFrm.class.getSimpleName())
          .result("evaluation/performance_data/sirs/FirstReactionMethod_grid_overhead_jmh.csv");
      opt.measurementIterations(numIterations)
          .warmupIterations(warmupIterations)
          .warmupTime(TimeValue.seconds(warmupTime))
          .measurementTime(TimeValue.seconds(measurementTime))
          .forks(numForks)
          .jvmArgs("-Xms2g", "-Xmx7g")
          .resultFormat(ResultFormatType.CSV)
          .build();
      new Runner(opt).run();
    }
  }

  /**
   * Test the time taken for initialisation of NRM, ODM and Vanilla. Also measure the first step of
   * FRM and LDM.
   *
   * @throws RunnerException When jmh fails.
   */
  private static void initPerformance(Method method) throws RunnerException {
    OptionsBuilder opt = new OptionsBuilder();
    switch (method) {
      case frm:
        opt.include(BenchmarkSirsInitFrm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/FirstReactionMethod_init_jmh.csv");
        break;
      case ldm:
        opt.include(BenchmarkSirsInitLdm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/LogarithmicDirectMethod_init_jmh.csv");
        break;
      case nrm:
        opt.include(BenchmarkSirsInitNrm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/NextReactionMethod_init_jmh.csv");
        break;
      case odm:
        opt.include(BenchmarkSirsInitOdm.class.getSimpleName())
            .result("evaluation/performance_data/sirs/OptimizedDirectMethod_init_jmh.csv");
        break;
      case vanilla:
        opt.include(BenchmarkSirsInitVanilla.class.getSimpleName())
            .result("evaluation/performance_data/sirs/Vanilla_init_jmh.csv");
        break;
      default:
        break;
    }
    opt.measurementIterations(40)
        .warmupIterations(10)
        .forks(5)
        .jvmArgs("-Xms2g", "-Xmx7g")
        .resultFormat(ResultFormatType.CSV)
        .build();
    new Runner(opt).run();
  }

  private static void sheepPerformance(Method method) throws RunnerException {
    OptionsBuilder opt = new OptionsBuilder();
    switch (method) {
      case frm:
        opt.include(BenchmarkSheepFrm.class.getSimpleName())
            .result("evaluation/performance_data/predatorprey/FirstReactionMethod_sheep_jmh.csv");
        break;
      case nrm:
        opt.include(BenchmarkSheepNrm.class.getSimpleName())
            .result("evaluation/performance_data/predatorprey/NextReactionMethod_sheep_jmh.csv");
        break;
      default:
        break;
    }
    opt.measurementIterations(numIterations)
        .warmupIterations(warmupIterations)
        .warmupTime(TimeValue.seconds(warmupTime))
        .measurementTime(TimeValue.seconds(measurementTime))
        .forks(numForks)
        .jvmArgs("-Xms2g", "-Xmx7g")
        .resultFormat(ResultFormatType.CSV)
        .build();
    new Runner(opt).run();
  }
}
