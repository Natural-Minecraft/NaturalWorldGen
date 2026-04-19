package id.naturalsmp.NaturalWorldGen.core.tools;


import id.naturalsmp.NaturalWorldGen.NaturalGenerator;
import id.naturalsmp.NaturalWorldGen.core.pregenerator.PregenTask;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisDimension;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import id.naturalsmp.NaturalWorldGen.util.collection.KMap;
import id.naturalsmp.NaturalWorldGen.util.exceptions.IrisException;
import id.naturalsmp.NaturalWorldGen.util.format.Form;
import id.naturalsmp.NaturalWorldGen.util.io.IO;
import id.naturalsmp.NaturalWorldGen.util.scheduling.J;
import id.naturalsmp.NaturalWorldGen.util.scheduling.PrecisionStopwatch;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;


public class IrisPackBenchmarking {
    private static final ThreadLocal<IrisPackBenchmarking> instance = new ThreadLocal<>();
    private final PrecisionStopwatch stopwatch = new PrecisionStopwatch();
    private final IrisDimension dimension;
    private final int radius;
    private final boolean gui;

    public IrisPackBenchmarking(IrisDimension dimension, int radius, boolean gui) {
        this.dimension = dimension;
        this.radius = radius;
        this.gui = gui;
        runBenchmark();
    }

    public static IrisPackBenchmarking getInstance() {
        return instance.get();
    }

    private void runBenchmark() {
        Thread.ofVirtual()
                .name("PackBenchmarking")
                .start(() -> {
                    NaturalGenerator.info("Setting up benchmark environment ");
                    IO.delete(new File(Bukkit.getWorldContainer(), "benchmark"));
                    createBenchmark();
                    while (!IrisToolbelt.isIrisWorld(Bukkit.getWorld("benchmark"))) {
                        J.sleep(1000);
                        NaturalGenerator.debug("NaturalWorldGen PackBenchmark: Waiting...");
                    }
                    NaturalGenerator.info("Starting Benchmark!");
                    stopwatch.begin();
                    startBenchmark();
                });

    }

    public void finishedBenchmark(KList<Integer> cps) {
        try {
            String time = Form.duration((long) stopwatch.getMilliseconds());
            Engine engine = IrisToolbelt.access(Bukkit.getWorld("benchmark")).getEngine();
            NaturalGenerator.info("-----------------");
            NaturalGenerator.info("Results:");
            NaturalGenerator.info("- Total time: " + time);
            NaturalGenerator.info("- Average CPS: " + calculateAverage(cps));
            NaturalGenerator.info("  - Median CPS: " + calculateMedian(cps));
            NaturalGenerator.info("  - Highest CPS: " + findHighest(cps));
            NaturalGenerator.info("  - Lowest CPS: " + findLowest(cps));
            NaturalGenerator.info("-----------------");
            NaturalGenerator.info("Creating a report..");
            File results = NaturalGenerator.instance.getDataFile("packbenchmarks", dimension.getName() + " " + LocalDateTime.now(Clock.systemDefaultZone()).toString().replace(':', '-') + ".txt");
            KMap<String, Double> metrics = engine.getMetrics().pull();
            try (FileWriter writer = new FileWriter(results)) {
                writer.write("-----------------\n");
                writer.write("Results:\n");
                writer.write("Dimension: " + dimension.getName() + "\n");
                writer.write("- Date of Benchmark: " + LocalDateTime.now(Clock.systemDefaultZone()) + "\n");
                writer.write("\n");
                writer.write("Metrics");
                for (String m : metrics.k()) {
                    double i = metrics.get(m);
                    writer.write("- " + m + ": " + i);
                }
                writer.write("- " + metrics);
                writer.write("Benchmark: " + LocalDateTime.now(Clock.systemDefaultZone()) + "\n");
                writer.write("- Total time: " + time + "\n");
                writer.write("- Average CPS: " + calculateAverage(cps) + "\n");
                writer.write("  - Median CPS: " + calculateMedian(cps) + "\n");
                writer.write("  - Highest CPS: " + findHighest(cps) + "\n");
                writer.write("  - Lowest CPS: " + findLowest(cps) + "\n");
                writer.write("-----------------\n");
                NaturalGenerator.info("Finished generating a report!");
            } catch (IOException e) {
                NaturalGenerator.error("An error occurred writing to the file.");
                e.printStackTrace();
            }

            J.s(() -> {
                var world = Bukkit.getWorld("benchmark");
                if (world == null) return;
                IrisToolbelt.evacuate(world);
                Bukkit.unloadWorld(world, true);
            });

            stopwatch.end();
        } catch (Exception e) {
            NaturalGenerator.error("Something has gone wrong!");
            e.printStackTrace();
        }
    }

    private void createBenchmark() {
        try {
            IrisToolbelt.createWorld()
                    .dimension(dimension.getLoadKey())
                    .name("benchmark")
                    .seed(1337)
                    .studio(false)
                    .benchmark(true)
                    .create();
        } catch (IrisException e) {
            throw new RuntimeException(e);
        }
    }

    private void startBenchmark() {
        try {
            instance.set(this);
            IrisToolbelt.pregenerate(PregenTask
                    .builder()
                    .gui(gui)
                    .radiusX(radius)
                    .radiusZ(radius)
                    .build(), Bukkit.getWorld("benchmark")
            );
        } finally {
            instance.remove();
        }
    }

    private double calculateAverage(KList<Integer> list) {
        double sum = 0;
        for (int num : list) {
            sum += num;
        }
        return sum / list.size();
    }

    private double calculateMedian(KList<Integer> list) {
        Collections.sort(list);
        int middle = list.size() / 2;

        if (list.size() % 2 == 1) {
            return list.get(middle);
        } else {
            return (list.get(middle - 1) + list.get(middle)) / 2.0;
        }
    }

    private int findLowest(KList<Integer> list) {
        return Collections.min(list);
    }

    private int findHighest(KList<Integer> list) {
        return Collections.max(list);
    }
}