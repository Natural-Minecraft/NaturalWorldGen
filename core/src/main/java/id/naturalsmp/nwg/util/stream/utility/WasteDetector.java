package id.naturalsmp.nwg.util.stream.utility;

import id.naturalsmp.nwg.NaturalGenerator;
import id.naturalsmp.nwg.util.collection.KList;
import id.naturalsmp.nwg.util.collection.KMap;
import id.naturalsmp.nwg.util.stream.BasicStream;
import id.naturalsmp.nwg.util.stream.ProceduralStream;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WasteDetector<T> extends BasicStream<T> {
    public static final boolean checking = false;
    private static final KMap<String, Integer> allAccesses = new KMap<>();
    private static final KMap<String, List<Throwable>> allThrows = new KMap<>();
    private final AtomicInteger accesses;
    private final String name;

    public WasteDetector(ProceduralStream<T> stream, String name) {
        super(stream);
        this.name = name;
        accesses = new AtomicInteger(0);
    }

    public static void printAll() {
        if (checking) {
            NaturalGenerator.warn("=========================================================");
            for (String i : allAccesses.sortKNumber().reverse()) {
                NaturalGenerator.warn(i + ": " + allAccesses.get(i) + " Time(s)");
            }
            NaturalGenerator.warn("=========================================================");
            for (String i : allAccesses.sortKNumber().reverse()) {
                NaturalGenerator.warn("======== " + i + " ========");
                for (Throwable j : allThrows.get(i)) {
                    j.printStackTrace();
                }
                NaturalGenerator.warn("---------------------------------------------------------");
            }
            NaturalGenerator.warn("=========================================================");
        }
    }

    @Override
    public T get(double x, double z) {
        if (checking) {
            if (x == 7 && z == 7) {
                // AHHHAAA!
                allAccesses.compute(name, (k, v) -> v == null ? 1 : v + 1);
                try {
                    throw new RuntimeException();
                } catch (RuntimeException e) {
                    allThrows.computeIfAbsent(name, (k) -> new KList<>()).add(e);
                }
            }

        }
        return getTypedSource().get(x, z);
    }

    @Override
    public T get(double x, double y, double z) {
        return getTypedSource().get(x, y, z);
    }

    @Override
    public double toDouble(T t) {
        return getTypedSource().toDouble(t);
    }

    @Override
    public T fromDouble(double d) {
        return getTypedSource().fromDouble(d);
    }
}
