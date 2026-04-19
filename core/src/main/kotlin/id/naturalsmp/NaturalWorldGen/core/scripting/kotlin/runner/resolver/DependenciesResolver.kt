package id.naturalsmp.NaturalWorldGen.core.scripting.kotlin.runner.resolver

import java.io.File
import kotlin.script.experimental.dependencies.ExternalDependenciesResolver

interface DependenciesResolver : ExternalDependenciesResolver {
    fun addPack(directory: File)
}