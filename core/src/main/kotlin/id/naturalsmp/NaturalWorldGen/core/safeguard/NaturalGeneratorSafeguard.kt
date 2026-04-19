package id.naturalsmp.NaturalWorldGen.core.safeguard

import id.naturalsmp.NaturalWorldGen.NaturalWorldGen
import id.naturalsmp.NaturalWorldGen.core.IrisSettings
import id.naturalsmp.NaturalWorldGen.core.safeguard.task.Diagnostic
import id.naturalsmp.NaturalWorldGen.core.safeguard.task.Task
import id.naturalsmp.NaturalWorldGen.core.safeguard.task.ValueWithDiagnostics
import id.naturalsmp.NaturalWorldGen.core.safeguard.task.tasks
import id.naturalsmp.NaturalWorldGen.util.format.C
import id.naturalsmp.NaturalWorldGen.util.scheduling.J
import org.bukkit.Bukkit
import java.util.*

object IrisSafeguard {
    @Volatile
    private var forceShutdown = false
    private var results: Map<Task, ValueWithDiagnostics<Mode>> = emptyMap()
    private var context: Map<String, String> = emptyMap()
    private var attachment: Map<String, List<String>> = emptyMap()
    private var mode = Mode.STABLE
    private var count = 0

    @JvmStatic
    fun execute() {
        val results = LinkedHashMap<Task, ValueWithDiagnostics<Mode>>(tasks.size)
        val context = LinkedHashMap<String, String>(tasks.size)
        val attachment = LinkedHashMap<String, List<String>>(tasks.size)
        var mode = Mode.STABLE
        var count = 0
        for (task in tasks) {
            var result: ValueWithDiagnostics<Mode>
            try {
                result = task.run()
            } catch (e: Throwable) {
                NaturalWorldGen.reportError(e)
                result = ValueWithDiagnostics(
                    Mode.WARNING,
                    Diagnostic(Diagnostic.Logger.ERROR, "Error while running task ${task.id}", e)
                )
            }
            mode = mode.highest(result.value)
            results[task] = result
            context[task.id] = result.value.id
            attachment[task.id] = result.diagnostics.flatMap { it.toString().split('\n') }
            if (result.value != Mode.STABLE) count++
        }

        this.results = Collections.unmodifiableMap(results)
        this.context = Collections.unmodifiableMap(context)
        this.attachment = Collections.unmodifiableMap(attachment)
        this.mode = mode
        this.count = count
    }

    @JvmStatic
    fun mode() = mode

    @JvmStatic
    fun asContext() = context

    @JvmStatic
    fun asAttachment() = attachment

    @JvmStatic
    fun splash() {
        NaturalGenerator.instance.splash()
        printReports()
        printFooter()
    }

    @JvmStatic
    fun printReports() {
        when (mode) {
            Mode.STABLE -> NaturalWorldGen.info(C.BLUE.toString() + "0 Conflicts found")
            Mode.WARNING -> NaturalWorldGen.warn(C.GOLD.toString() + "%s Issues found", count)
            Mode.UNSTABLE -> NaturalWorldGen.error(C.DARK_RED.toString() + "%s Issues found", count)
        }

        results.values.forEach { it.log(withStackTrace = true) }
    }

    @JvmStatic
    fun printFooter() {
        when (mode) {
            Mode.STABLE -> NaturalWorldGen.info(C.BLUE.toString() + "NaturalWorldGen is running Stable")
            Mode.WARNING -> warning()
            Mode.UNSTABLE -> unstable()
        }
    }

    @JvmStatic
    fun isForceShutdown() = forceShutdown

    private fun warning() {
        NaturalWorldGen.warn(C.GOLD.toString() + "NaturalWorldGen is running in Warning Mode")

        NaturalWorldGen.warn("")
        NaturalWorldGen.warn(C.DARK_GRAY.toString() + "--==<" + C.GOLD + " IMPORTANT " + C.DARK_GRAY + ">==--")
        NaturalWorldGen.warn(C.GOLD.toString() + "NaturalWorldGen is running in warning mode which may cause the following issues:")
        NaturalWorldGen.warn("- Data Loss")
        NaturalWorldGen.warn("- Errors")
        NaturalWorldGen.warn("- Broken worlds")
        NaturalWorldGen.warn("- Unexpected behavior.")
        NaturalWorldGen.warn("- And perhaps further complications.")
        NaturalWorldGen.warn("")
    }

    private fun unstable() {
        NaturalWorldGen.error(C.DARK_RED.toString() + "NaturalWorldGen is running in Unstable Mode")

        NaturalWorldGen.error("")
        NaturalWorldGen.error(C.DARK_GRAY.toString() + "--==<" + C.RED + " IMPORTANT " + C.DARK_GRAY + ">==--")
        NaturalWorldGen.error("NaturalWorldGen is running in unstable mode which may cause the following issues:")
        NaturalWorldGen.error(C.DARK_RED.toString() + "Server Issues")
        NaturalWorldGen.error("- Server won't boot")
        NaturalWorldGen.error("- Data Loss")
        NaturalWorldGen.error("- Unexpected behavior.")
        NaturalWorldGen.error("- And More...")
        NaturalWorldGen.error(C.DARK_RED.toString() + "World Issues")
        NaturalWorldGen.error("- Worlds can't load due to corruption.")
        NaturalWorldGen.error("- Worlds may slowly corrupt until they can't load.")
        NaturalWorldGen.error("- World data loss.")
        NaturalWorldGen.error("- And More...")
        NaturalWorldGen.error(C.DARK_RED.toString() + "ATTENTION: " + C.RED + "While running NaturalWorldGen in unstable mode, you won't be eligible for support.")

        if (IrisSettings.get().general.isDoomsdayAnnihilationSelfDestructMode) {
            NaturalWorldGen.error(C.DARK_RED.toString() + "Boot Unstable is set to true, continuing with the startup process in 10 seconds.")
            J.sleep(10000L)
        } else {
            NaturalWorldGen.error(C.DARK_RED.toString() + "Go to plugins/naturalworldgen/settings.json and set DoomsdayAnnihilationSelfDestructMode to true if you wish to proceed.")
            NaturalWorldGen.error(C.DARK_RED.toString() + "The server will shutdown in 10 seconds.")
            J.sleep(10000L)
            NaturalWorldGen.error(C.DARK_RED.toString() + "Shutting down server.")
            forceShutdown = true
            try {
                Bukkit.getPluginManager().disablePlugins()
            } finally {
                Runtime.getRuntime().halt(42)
            }
        }
        NaturalWorldGen.info("")
    }
}