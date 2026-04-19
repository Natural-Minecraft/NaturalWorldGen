package id.naturalsmp.NaturalWorldGen.core.scripting.kotlin.base

import id.naturalsmp.NaturalWorldGen.core.scripting.func.BiomeLookup
import id.naturalsmp.NaturalWorldGen.engine.IrisComplex
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine
import id.naturalsmp.NaturalWorldGen.engine.`object`.IrisDimension
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.providedProperties

@KotlinScript(fileExtension = "engine.kts", compilationConfiguration = EngineScriptDefinition::class)
abstract class EngineScript

object EngineScriptDefinition : ScriptCompilationConfiguration(listOf(DataScriptDefinition), {
    providedProperties(
        "engine" to Engine::class,
        "seed" to Long::class,
        "dimension" to IrisDimension::class,
        "complex" to IrisComplex::class,
        "biome" to BiomeLookup::class,
    )
}) {

    private fun readResolve(): Any = EngineScriptDefinition
}