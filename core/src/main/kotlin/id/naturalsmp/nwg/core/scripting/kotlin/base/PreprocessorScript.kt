package id.naturalsmp.nwg.core.scripting.kotlin.base

import id.naturalsmp.nwg.core.loader.IrisRegistrant
import id.naturalsmp.nwg.engine.framework.Engine
import id.naturalsmp.nwg.engine.`object`.IrisDimension
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.providedProperties

@KotlinScript(fileExtension = "proc.kts", compilationConfiguration = PreprocessorScriptDefinition::class)
abstract class PreprocessorScript

object PreprocessorScriptDefinition : ScriptCompilationConfiguration(listOf(DataScriptDefinition), {
    providedProperties(
        "engine" to Engine::class,
        "seed" to Long::class,
        "dimension" to IrisDimension::class,
        "object" to IrisRegistrant::class
    )
}) {
    private fun readResolve(): Any = PreprocessorScriptDefinition
}