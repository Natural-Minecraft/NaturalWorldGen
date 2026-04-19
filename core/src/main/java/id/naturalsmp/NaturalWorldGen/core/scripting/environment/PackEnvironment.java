package id.naturalsmp.NaturalWorldGen.core.scripting.environment;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.scripting.kotlin.environment.IrisPackExecutionEnvironment;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.util.math.RNG;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface PackEnvironment extends SimpleEnvironment {
    static PackEnvironment create(@NonNull IrisData data) {
        return new IrisPackExecutionEnvironment(data);
    }

    @NonNull
    IrisData getData();

    @Nullable
    Object createNoise(@NonNull String script, @NonNull RNG rng);

    EngineEnvironment with(@NonNull Engine engine);
}