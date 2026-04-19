package id.naturalsmp.nwg.core.scripting.kotlin.environment

import id.naturalsmp.nwg.core.loader.IrisRegistrant
import id.naturalsmp.nwg.core.scripting.environment.EngineEnvironment
import id.naturalsmp.nwg.core.scripting.func.BiomeLookup
import id.naturalsmp.nwg.core.scripting.func.UpdateExecutor
import id.naturalsmp.nwg.core.scripting.kotlin.base.ChunkUpdateScript
import id.naturalsmp.nwg.core.scripting.kotlin.base.EngineScript
import id.naturalsmp.nwg.core.scripting.kotlin.base.MobSpawningScript
import id.naturalsmp.nwg.core.scripting.kotlin.base.PostMobSpawningScript
import id.naturalsmp.nwg.core.scripting.kotlin.base.PreprocessorScript
import id.naturalsmp.nwg.core.scripting.kotlin.environment.IrisSimpleExecutionEnvironment
import id.naturalsmp.nwg.core.scripting.kotlin.runner.ScriptRunner
import id.naturalsmp.nwg.engine.framework.Engine
import id.naturalsmp.nwg.toolbelt.mantle.MantleChunk
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.io.File

class IrisExecutionEnvironment internal constructor(
    private val engine: Engine,
    parent: ScriptRunner?,
) : IrisPackExecutionEnvironment(engine.data, parent), EngineEnvironment {
    constructor(engine: Engine) : this(engine, null)
    override fun getEngine() = engine

    override fun execute(script: String) =
        execute(script, EngineScript::class.java, engine.parameters())

    override fun evaluate(script: String) =
        evaluate(script, EngineScript::class.java, engine.parameters())

    override fun spawnMob(script: String, location: Location) =
        evaluate(script, MobSpawningScript::class.java, engine.parameters("location" to location))

    override fun postSpawnMob(script: String, location: Location, mob: Entity) =
        execute(script, PostMobSpawningScript::class.java, engine.parameters("location" to location, "entity" to mob))

    override fun preprocessObject(script: String, `object`: IrisRegistrant) =
        execute(script, PreprocessorScript::class.java, engine.limitedParameters("object" to `object`))

    override fun updateChunk(script: String, mantleChunk: MantleChunk, chunk: Chunk, executor: UpdateExecutor) =
        execute(script, ChunkUpdateScript::class.java, engine.parameters("mantleChunk" to mantleChunk, "chunk" to chunk, "executor" to executor))

    private fun Engine.limitedParameters(vararg values: Pair<String, Any?>): Map<String, Any?> {
        return mapOf(
            "data" to data,
            "engine" to this,
            "seed" to seedManager.seed,
            "dimension" to dimension,
            *values,
        )
    }

    private fun Engine.parameters(vararg values: Pair<String, Any?>): Map<String, Any?> {
        return limitedParameters(
            "complex" to complex,
            "biome" to BiomeLookup(::getSurfaceBiome),
            *values,
        )
    }
}