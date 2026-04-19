package id.naturalsmp.nwg.engine.framework;

import id.naturalsmp.nwg.core.loader.IrisData;

import java.util.function.Function;

public interface ListFunction<R> extends Function<IrisData, R> {
    String key();
    String fancyName();
}
