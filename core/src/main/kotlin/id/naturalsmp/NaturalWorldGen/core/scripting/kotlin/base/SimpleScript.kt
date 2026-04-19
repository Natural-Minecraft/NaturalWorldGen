package id.naturalsmp.NaturalWorldGen.core.scripting.kotlin.base

import id.naturalsmp.NaturalWorldGen.core.scripting.kotlin.runner.configure
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(fileExtension = "simple.kts", compilationConfiguration = SimpleScriptDefinition::class)
abstract class SimpleScript

object SimpleScriptDefinition : ScriptCompilationConfiguration({
    defaultImports(
        DependsOn::class.qualifiedName!!,
        Repository::class.qualifiedName!!,
        "id.naturalsmp.NaturalWorldGen.NaturalGenerator.info",
        "id.naturalsmp.NaturalWorldGen.NaturalGenerator.debug",
        "id.naturalsmp.NaturalWorldGen.NaturalGenerator.warn",
        "id.naturalsmp.NaturalWorldGen.NaturalGenerator.error"
    )

    jvm {
        dependenciesFromClassContext(KotlinScript::class, wholeClasspath = true)
        dependenciesFromClassContext(SimpleScript::class, wholeClasspath = true)
    }

    configure()
}) {
    private fun readResolve(): Any = SimpleScriptDefinition
}