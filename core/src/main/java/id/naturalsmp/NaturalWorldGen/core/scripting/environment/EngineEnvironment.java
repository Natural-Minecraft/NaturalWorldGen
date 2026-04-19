package id.naturalsmp.NaturalWorldGen.core.scripting.environment;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisRegistrant;
import id.naturalsmp.NaturalWorldGen.core.scripting.func.UpdateExecutor;
import id.naturalsmp.NaturalWorldGen.core.scripting.kotlin.environment.IrisExecutionEnvironment;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.util.mantle.MantleChunk;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EngineEnvironment extends PackEnvironment {
    static EngineEnvironment create(@NonNull Engine engine) {
        return new IrisExecutionEnvironment(engine);
    }

    @NonNull
    Engine getEngine();

    @Nullable
    Object spawnMob(@NonNull String script, @NonNull Location location);

    void postSpawnMob(@NonNull String script, @NonNull Location location, @NonNull Entity mob);

    void preprocessObject(@NonNull String script, @NonNull IrisRegistrant object);

    void updateChunk(@NonNull String script, @NonNull MantleChunk mantleChunk, @NonNull Chunk chunk, @NonNull UpdateExecutor executor);
}