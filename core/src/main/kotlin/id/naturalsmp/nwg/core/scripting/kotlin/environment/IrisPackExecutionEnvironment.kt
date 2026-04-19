package id.naturalsmp.nwg.core.scripting.kotlin.environment

import id.naturalsmp.nwg.core.loader.IrisData
import id.naturalsmp.nwg.core.scripting.environment.EngineEnvironment
import id.naturalsmp.nwg.core.scripting.environment.PackEnvironment
import id.naturalsmp.nwg.core.scripting.kotlin.base.DataScript
import id.naturalsmp.nwg.core.scripting.kotlin.base.NoiseScript
import id.naturalsmp.nwg.core.scripting.kotlin.runner.Script
import id.naturalsmp.nwg.core.scripting.kotlin.runner.ScriptRunner
import id.naturalsmp.nwg.core.scripting.kotlin.runner.valueOrThrow
import id.naturalsmp.nwg.engine.framework.Engine
import id.naturalsmp.nwg.toolbelt.math.RNG
import kotlin.reflect.KClass
import java.util.function.Function

open class IrisPackExecutionEnvironment internal constructor(
    private val data: IrisData,
    parent: ScriptRunner?
) : IrisSimpleExecutionEnvironment(data.dataFolder, parent), PackEnvironment {
    constructor(data: IrisData) : this(data, null)

    override fun getData() = data

    override fun compile(script: String, type: KClass<*>): Script {
        val loaded = data.scriptLoader.load(script)
        return compileCache.get(script)
            .computeIfAbsent(type, Function { _ -> runner.compile(type, loaded.loadFile, loaded.source) })
            .valueOrThrow("Failed to compile script $script")
    }

    override fun execute(script: String) =
        execute(script, DataScript::class.java, data.parameters())

    override fun evaluate(script: String) =
        evaluate(script, DataScript::class.java, data.parameters())

    override fun createNoise(script: String, rng: RNG) =
        evaluate(script, NoiseScript::class.java, data.parameters("rng" to rng))

    override fun with(engine: Engine) =
        IrisExecutionEnvironment(engine, runner)

    private fun IrisData.parameters(vararg values: Pair<String, Any?>): Map<String, Any?> {
        return mapOf(
            "data" to this,
            *values,
        )
    }
}