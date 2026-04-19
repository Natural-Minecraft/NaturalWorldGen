package id.naturalsmp.nwg.core.safeguard

import id.naturalsmp.nwg.NaturalGenerator
import id.naturalsmp.nwg.core.IrisSettings
import id.naturalsmp.nwg.core.safeguard.task.Diagnostic
import id.naturalsmp.nwg.core.safeguard.task.Task
import id.naturalsmp.nwg.core.safeguard.task.ValueWithDiagnostics
import id.naturalsmp.nwg.core.safeguard.task.tasks
import id.naturalsmp.nwg.utilities.format.C
import id.naturalsmp.nwg.utilities.scheduling.J
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
                NaturalGenerator.reportError(e)
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
            Mode.STABLE -> NaturalGenerator.info(C.BLUE.toString() + "0 Conflicts found")
            Mode.WARNING -> NaturalGenerator.warn(C.GOLD.toString() + "%s Issues found", count)
            Mode.UNSTABLE -> NaturalGenerator.error(C.DARK_RED.toString() + "%s Issues found", count)
        }

        results.values.forEach { it.log(withStackTrace = true) }
    }

    @JvmStatic
    fun printFooter() {
        when (mode) {
            Mode.STABLE -> NaturalGenerator.info(C.BLUE.toString() + "NaturalGenerator is running Stable")
            Mode.WARNING -> warning()
            Mode.UNSTABLE -> unstable()
        }
    }

    @JvmStatic
    fun isForceShutdown() = forceShutdown

    private fun warning() {
        NaturalGenerator.warn(C.GOLD.toString() + "NaturalGenerator is running in Warning Mode")

        NaturalGenerator.warn("")
        NaturalGenerator.warn(C.DARK_GRAY.toString() + "--==<" + C.GOLD + " IMPORTANT " + C.DARK_GRAY + ">==--")
        NaturalGenerator.warn(C.GOLD.toString() + "NaturalGenerator is running in warning mode which may cause the following issues:")
        NaturalGenerator.warn("- Data Loss")
        NaturalGenerator.warn("- Errors")
        NaturalGenerator.warn("- Broken worlds")
        NaturalGenerator.warn("- Unexpected behavior.")
        NaturalGenerator.warn("- And perhaps further complications.")
        NaturalGenerator.warn("")
    }

    private fun unstable() {
        NaturalGenerator.error(C.DARK_RED.toString() + "NaturalGenerator is running in Unstable Mode")

        NaturalGenerator.error("")
        NaturalGenerator.error(C.DARK_GRAY.toString() + "--==<" + C.RED + " IMPORTANT " + C.DARK_GRAY + ">==--")
        NaturalGenerator.error("NaturalGenerator is running in unstable mode which may cause the following issues:")
        NaturalGenerator.error(C.DARK_RED.toString() + "Server Issues")
        NaturalGenerator.error("- Server won't boot")
        NaturalGenerator.error("- Data Loss")
        NaturalGenerator.error("- Unexpected behavior.")
        NaturalGenerator.error("- And More...")
        NaturalGenerator.error(C.DARK_RED.toString() + "World Issues")
        NaturalGenerator.error("- Worlds can't load due to corruption.")
        NaturalGenerator.error("- Worlds may slowly corrupt until they can't load.")
        NaturalGenerator.error("- World data loss.")
        NaturalGenerator.error("- And More...")
        NaturalGenerator.error(C.DARK_RED.toString() + "ATTENTION: " + C.RED + "While running NaturalGenerator in unstable mode, you won't be eligible for support.")

        if (IrisSettings.get().general.isDoomsdayAnnihilationSelfDestructMode) {
            NaturalGenerator.error(C.DARK_RED.toString() + "Boot Unstable is set to true, continuing with the startup process in 10 seconds.")
            J.sleep(10000L)
        } else {
            NaturalGenerator.error(C.DARK_RED.toString() + "Go to plugins/naturalworldgen/settings.json and set DoomsdayAnnihilationSelfDestructMode to true if you wish to proceed.")
            NaturalGenerator.error(C.DARK_RED.toString() + "The server will shutdown in 10 seconds.")
            J.sleep(10000L)
            NaturalGenerator.error(C.DARK_RED.toString() + "Shutting down server.")
            forceShutdown = true
            try {
                Bukkit.getPluginManager().disablePlugins()
            } finally {
                Runtime.getRuntime().halt(42)
            }
        }
        NaturalGenerator.info("")
    }
}