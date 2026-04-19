package id.naturalsmp.NaturalWorldGen.engine.framework;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;

import java.util.function.Function;

public interface ListFunction<R> extends Function<IrisData, R> {
    String key();
    String fancyName();
}
