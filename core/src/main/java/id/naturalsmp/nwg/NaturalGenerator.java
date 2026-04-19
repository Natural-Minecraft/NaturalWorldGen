/*
 * NaturalGenerator is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2022 Arcane Arts (NaturalDev Software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package id.naturalsmp.nwg;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import id.naturalsmp.nwg.core.IrisSettings;
import id.naturalsmp.nwg.core.IrisWorlds;
import id.naturalsmp.nwg.core.ServerConfigurator;
import id.naturalsmp.nwg.core.link.IrisPapiExpansion;
import id.naturalsmp.nwg.core.link.MultiverseCoreLink;
import id.naturalsmp.nwg.core.loader.IrisData;
import id.naturalsmp.nwg.core.nms.INMS;
import id.naturalsmp.nwg.core.pregenerator.LazyPregenerator;
import id.naturalsmp.nwg.core.service.StudioSVC;
import id.naturalsmp.nwg.core.tools.IrisToolbelt;
import id.naturalsmp.nwg.engine.EnginePanic;
import id.naturalsmp.nwg.engine.object.IrisCompat;
import id.naturalsmp.nwg.engine.object.IrisDimension;
import id.naturalsmp.nwg.engine.object.IrisWorld;
import id.naturalsmp.nwg.engine.platform.BukkitChunkGenerator;
import id.naturalsmp.nwg.core.safeguard.IrisSafeguard;
import id.naturalsmp.nwg.engine.platform.PlatformChunkGenerator;
import id.naturalsmp.nwg.util.collection.KList;
import id.naturalsmp.nwg.util.collection.KMap;
import id.naturalsmp.nwg.util.exceptions.IrisException;
import id.naturalsmp.nwg.util.format.C;
import id.naturalsmp.nwg.util.function.NastyRunnable;
import id.naturalsmp.nwg.util.io.FileWatcher;
import id.naturalsmp.nwg.util.io.IO;
import id.naturalsmp.nwg.util.io.InstanceState;
import id.naturalsmp.nwg.util.io.JarScanner;
import id.naturalsmp.nwg.util.math.M;
import id.naturalsmp.nwg.util.math.RNG;
import id.naturalsmp.nwg.util.misc.Bindings;
import id.naturalsmp.nwg.util.misc.SlimJar;
import id.naturalsmp.nwg.util.parallel.MultiBurst;
import id.naturalsmp.nwg.util.plugin.IrisService;
import id.naturalsmp.nwg.util.plugin.NaturalDevPlugin;
import id.naturalsmp.nwg.util.plugin.NaturalDevSender;
import id.naturalsmp.nwg.util.plugin.chunk.ChunkTickets;
import id.naturalsmp.nwg.util.scheduling.J;
import id.naturalsmp.nwg.util.scheduling.Queue;
import id.naturalsmp.nwg.util.scheduling.ShurikenQueue;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("CanBeFinal")
public class NaturalGenerator extends NaturalDevPlugin implements Listener {
    private static final Queue<Runnable> syncJobs = new ShurikenQueue<>();

    public static NaturalGenerator instance;
    public static Bindings.Adventure audiences;
    public static MultiverseCoreLink linkMultiverseCore;
    public static IrisCompat compat;
    public static FileWatcher configWatcher;
    public static ChunkTickets tickets;
    private static NaturalDevSender sender;
    private static Thread shutdownHook;

    static {
        try {
            InstanceState.updateInstanceId();
        } catch (Throwable ignored) {

        }
    }

    private final KList<Runnable> postShutdown = new KList<>();
    private KMap<Class<? extends IrisService>, IrisService> services;

    public static NaturalDevSender getSender() {
        if (sender == null) {
            sender = new NaturalDevSender(Bukkit.getConsoleSender());
            sender.setTag(instance.getTag());
        }
        return sender;
    }

    @SuppressWarnings("unchecked")
    public static <T> T service(Class<T> c) {
        return (T) instance.services.get(c);
    }

    public static void callEvent(Event e) {
        if (!e.isAsynchronous()) {
            J.s(() -> Bukkit.getPluginManager().callEvent(e));
        } else {
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    public static KList<Object> initialize(String s, Class<? extends Annotation> slicedClass) {
        JarScanner js = new JarScanner(instance.getJarFile(), s);
        KList<Object> v = new KList<>();
        J.attempt(js::scan);
        for (Class<?> i : js.getClasses()) {
            if (slicedClass == null || i.isAnnotationPresent(slicedClass)) {
                try {
                    v.add(i.getDeclaredConstructor().newInstance());
                } catch (Throwable ignored) {

                }
            }
        }

        return v;
    }

    public static KList<Class<?>> getClasses(String s, Class<? extends Annotation> slicedClass) {
        JarScanner js = new JarScanner(instance.getJarFile(), s);
        KList<Class<?>> v = new KList<>();
        J.attempt(js::scan);
        for (Class<?> i : js.getClasses()) {
            if (slicedClass == null || i.isAnnotationPresent(slicedClass)) {
                try {
                    v.add(i);
                } catch (Throwable ignored) {

                }
            }
        }

        return v;
    }

    public static KList<Object> initialize(String s) {
        return initialize(s, null);
    }

    public static void sq(Runnable r) {
        synchronized (syncJobs) {
            syncJobs.queue(r);
        }
    }

    public static File getTemp() {
        return instance.getDataFolder("cache", "temp");
    }

    public static void msg(String string) {
        try {
            getSender().sendMessage(string);
        } catch (Throwable e) {
            try {
                instance.getLogger().info(instance.getTag() + string.replaceAll("(<([^>]+)>)", ""));
            } catch (Throwable ignored1) {

            }
        }
    }

    public static File getCached(String name, String url) {
        String h = IO.hash(name + "@" + url);
        File f = NaturalGenerator.instance.getDataFile("cache", h.substring(0, 2), h.substring(3, 5), h);

        if (!f.exists()) {
            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(f)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    NaturalGenerator.verbose("Aquiring " + name);
                }
            } catch (IOException e) {
                NaturalGenerator.reportError(e);
            }
        }

        return f.exists() ? f : null;
    }

    public static String getNonCached(String name, String url) {
        String h = IO.hash(name + "*" + url);
        File f = NaturalGenerator.instance.getDataFile("cache", h.substring(0, 2), h.substring(3, 5), h);

        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(f)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            NaturalGenerator.reportError(e);
        }

        try {
            return IO.readAll(f);
        } catch (IOException e) {
            NaturalGenerator.reportError(e);
        }

        return "";
    }

    public static File getNonCachedFile(String name, String url) {
        String h = IO.hash(name + "*" + url);
        File f = NaturalGenerator.instance.getDataFile("cache", h.substring(0, 2), h.substring(3, 5), h);
        NaturalGenerator.verbose("Download " + name + " -> " + url);
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(f)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            NaturalGenerator.reportError(e);
        }

        return f;
    }

    public static void warn(String format, Object... objs) {
        msg(C.YELLOW + String.format(format, objs));
    }

    public static void error(String format, Object... objs) {
        msg(C.RED + String.format(format, objs));
    }

    public static void debug(String string) {
        if (!IrisSettings.get().getGeneral().isDebug()) {
            return;
        }

        try {
            throw new RuntimeException();
        } catch (Throwable e) {
            try {
                String[] cc = e.getStackTrace()[1].getClassName().split("\\Q.\\E");

                if (cc.length > 5) {
                    debug(cc[3] + "/" + cc[4] + "/" + cc[cc.length - 1], e.getStackTrace()[1].getLineNumber(), string);
                } else {
                    debug(cc[3] + "/" + cc[4], e.getStackTrace()[1].getLineNumber(), string);
                }
            } catch (Throwable ex) {
                debug("Origin", -1, string);
            }
        }
    }

    public static void debug(String category, int line, String string) {
        if (!IrisSettings.get().getGeneral().isDebug()) {
            return;
        }
        if (IrisSettings.get().getGeneral().isUseConsoleCustomColors()) {
            msg("<gradient:#095fe0:#a848db>" + category + " <#bf3b76>" + line + "<reset> " + C.LIGHT_PURPLE + string.replaceAll("\\Q<\\E", "[").replaceAll("\\Q>\\E", "]"));
        } else {
            msg(C.BLUE + category + ":" + C.AQUA + line + C.RESET + C.LIGHT_PURPLE + " " + string.replaceAll("\\Q<\\E", "[").replaceAll("\\Q>\\E", "]"));

        }
    }

    public static void verbose(String string) {
        debug(string);
    }

    public static void success(String string) {
        msg(C.IRIS + string);
    }

    public static void info(String format, Object... args) {
        msg(C.WHITE + String.format(format, args));
    }

    @SuppressWarnings("deprecation")
    public static void later(NastyRunnable object) {
        try {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, () ->
            {
                try {
                    object.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                    NaturalGenerator.reportError(e);
                }
            }, RNG.r.i(100, 1200));
        } catch (IllegalPluginAccessException ignored) {

        }
    }

    public static int jobCount() {
        return syncJobs.size();
    }

    public static void clearQueues() {
        synchronized (syncJobs) {
            syncJobs.clear();
        }
    }

    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    public static String getJava() {
        String javaRuntimeName = System.getProperty("java.vm.name");
        String javaRuntimeVendor = System.getProperty("java.vendor");
        String javaRuntimeVersion = System.getProperty("java.vm.version");
        return String.format("%s %s (build %s)", javaRuntimeName, javaRuntimeVendor, javaRuntimeVersion);
    }

    public static void reportErrorChunk(int x, int z, Throwable e, String extra) {
        if (IrisSettings.get().getGeneral().isDebug()) {
            File f = instance.getDataFile("debug", "chunk-errors", "chunk." + x + "." + z + ".txt");

            if (!f.exists()) {
                J.attempt(() -> {
                    PrintWriter pw = new PrintWriter(f);
                    pw.println("Thread: " + Thread.currentThread().getName());
                    pw.println("First: " + new Date(M.ms()));
                    e.printStackTrace(pw);
                    pw.close();
                });
            }

            NaturalGenerator.debug("Chunk " + x + "," + z + " Exception Logged: " + e.getClass().getSimpleName() + ": " + C.RESET + "" + C.LIGHT_PURPLE + e.getMessage());
        }
    }

    public static void reportError(Throwable e) {
        Bindings.capture(e);
        if (IrisSettings.get().getGeneral().isDebug()) {
            String n = e.getClass().getCanonicalName() + "-" + e.getStackTrace()[0].getClassName() + "-" + e.getStackTrace()[0].getLineNumber();

            if (e.getCause() != null) {
                n += "-" + e.getCause().getStackTrace()[0].getClassName() + "-" + e.getCause().getStackTrace()[0].getLineNumber();
            }

            File f = instance.getDataFile("debug", "caught-exceptions", n + ".txt");

            if (!f.exists()) {
                J.attempt(() -> {
                    PrintWriter pw = new PrintWriter(f);
                    pw.println("Thread: " + Thread.currentThread().getName());
                    pw.println("First: " + new Date(M.ms()));
                    e.printStackTrace(pw);
                    pw.close();
                });
            }

            NaturalGenerator.debug("Exception Logged: " + e.getClass().getSimpleName() + ": " + C.RESET + "" + C.LIGHT_PURPLE + e.getMessage());
        }
    }

    public static void dump() {
        try {
            File fi = NaturalGenerator.instance.getDataFile("dump", "td-" + new java.sql.Date(M.ms()) + ".txt");
            FileOutputStream fos = new FileOutputStream(fi);
            Map<Thread, StackTraceElement[]> f = Thread.getAllStackTraces();
            PrintWriter pw = new PrintWriter(fos);
            for (Thread i : f.keySet()) {
                pw.println("========================================");
                pw.println("Thread: '" + i.getName() + "' ID: " + i.getId() + " STATUS: " + i.getState().name());

                for (StackTraceElement j : f.get(i)) {
                    pw.println("    @ " + j.toString());
                }

                pw.println("========================================");
                pw.println();
                pw.println();
            }
            pw.println("[%%__USER__%%,%%__RESOURCE__%%,%%__PRODUCT__%%,%%__BUILTBYBIT__%%]");

            pw.close();
            NaturalGenerator.info("DUMPED! See " + fi.getAbsolutePath());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void panic() {
        EnginePanic.panic();
    }

    public static void addPanic(String s, String v) {
        EnginePanic.add(s, v);
    }

    public NaturalGenerator() {
        instance = this;
        SlimJar.load();
    }

    private void enable() {
        services = new KMap<>();
        setupAudience();
        Bindings.setupSentry();
        initialize("id.naturalsmp.nwg.core.service").forEach((i) -> services.put((Class<? extends IrisService>) i.getClass(), (IrisService) i));
        IO.delete(new File("nwg"));
        compat = IrisCompat.configured(getDataFile("compat.json"));
        ServerConfigurator.configure();
        IrisSafeguard.execute();
        getSender().setTag(getTag());
        IrisSafeguard.splash();
        tickets = new ChunkTickets();
        linkMultiverseCore = new MultiverseCoreLink();
        configWatcher = new FileWatcher(getDataFile("settings.json"));
        services.values().forEach(IrisService::onEnable);
        services.values().forEach(this::registerListener);
        addShutdownHook();
        J.s(() -> {
            J.a(() -> IO.delete(getTemp()));
            J.a(LazyPregenerator::loadLazyGenerators, 100);
            J.a(this::bstats);
            J.ar(this::checkConfigHotload, 60);
            J.sr(this::tickQueue, 0);
            J.s(this::setupPapi);
            J.a(ServerConfigurator::configure, 20);

            autoStartStudio();
            checkForBukkitWorlds(s -> true);
            IrisToolbelt.retainMantleDataForSlice(String.class.getCanonicalName());
            IrisToolbelt.retainMantleDataForSlice(BlockData.class.getCanonicalName());
        });
    }

    public void addShutdownHook() {
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
        shutdownHook = new Thread(() -> {
            Bukkit.getWorlds()
                    .stream()
                    .map(IrisToolbelt::access)
                    .filter(Objects::nonNull)
                    .forEach(PlatformChunkGenerator::close);

            MultiBurst.burst.close();
            MultiBurst.ioBurst.close();
            services.clear();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public void checkForBukkitWorlds(Predicate<String> filter) {
        try {
            IrisWorlds.readBukkitWorlds().forEach((s, generator) -> {
                try {
                    if (Bukkit.getWorld(s) != null || !filter.test(s)) return;

                    NaturalGenerator.info("Loading World: %s | Generator: %s", s, generator);
                    var gen = getDefaultWorldGenerator(s, generator);
                    var dim = loadDimension(s, generator);
                    assert dim != null && gen != null;

                    NaturalGenerator.info(C.LIGHT_PURPLE + "Preparing Spawn for " + s + "' using NaturalGenerator:" + generator + "...");
                    WorldCreator c = new WorldCreator(s)
                            .generator(gen)
                            .environment(dim.getEnvironment());
                    INMS.get().createWorld(c);
                    NaturalGenerator.info(C.LIGHT_PURPLE + "Loaded " + s + "!");
                } catch (Throwable e) {
                    NaturalGenerator.error("Failed to load world " + s + "!");
                    e.printStackTrace();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            reportError(e);
        }
    }

    private void autoStartStudio() {
        if (IrisSettings.get().getStudio().isAutoStartDefaultStudio()) {
            NaturalGenerator.info("Starting up auto Studio!");
            try {
                Player r = new KList<>(getServer().getOnlinePlayers()).getRandom();
                NaturalGenerator.service(StudioSVC.class).open(r != null ? new NaturalDevSender(r) : getSender(), 1337, IrisSettings.get().getGenerator().getDefaultWorldType(), (w) -> {
                    J.s(() -> {
                        var spawn = w.getSpawnLocation();
                        for (Player i : getServer().getOnlinePlayers()) {
                            i.setGameMode(GameMode.SPECTATOR);
                            i.teleport(spawn);
                        }
                    });
                });
            } catch (IrisException e) {
                reportError(e);
            }
        }
    }

    private void setupAudience() {
        try {
            audiences = new Bindings.Adventure(this);
        } catch (Throwable e) {
            e.printStackTrace();
            IrisSettings.get().getGeneral().setUseConsoleCustomColors(false);
            IrisSettings.get().getGeneral().setUseCustomColorsIngame(false);
            NaturalGenerator.error("Failed to setup Adventure API... No custom colors :(");
        }
    }

    public void postShutdown(Runnable r) {
        postShutdown.add(r);
    }

    public void onEnable() {
        enable();
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        if (IrisSafeguard.isForceShutdown()) return;
        services.values().forEach(IrisService::onDisable);
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll((Plugin) this);
        postShutdown.forEach(Runnable::run);
        super.onDisable();

        J.attempt(new JarScanner(instance.getJarFile(), "", false)::scanAll);
    }

    private void setupPapi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new IrisPapiExpansion().register();
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getTag(String subTag) {
        return IrisSafeguard.mode().tag(subTag);
    }

    private void checkConfigHotload() {
        if (configWatcher.checkModified()) {
            IrisSettings.invalidate();
            IrisSettings.get();
            configWatcher.checkModified();
            NaturalGenerator.info("Hotloaded settings.json ");
        }
    }

    private void tickQueue() {
        synchronized (NaturalGenerator.syncJobs) {
            if (!NaturalGenerator.syncJobs.hasNext()) {
                return;
            }

            long ms = M.ms();

            while (NaturalGenerator.syncJobs.hasNext() && M.ms() - ms < 25) {
                try {
                    NaturalGenerator.syncJobs.next().run();
                } catch (Throwable e) {
                    e.printStackTrace();
                    NaturalGenerator.reportError(e);
                }
            }
        }
    }

    private void bstats() {
        if (IrisSettings.get().getGeneral().isPluginMetrics()) {
            Bindings.setupBstats(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    public void imsg(CommandSender s, String msg) {
        s.sendMessage(C.IRIS + "[" + C.DARK_GRAY + "NaturalGenerator" + C.IRIS + "]" + C.GRAY + ": " + msg);
    }

    @Nullable
    @Override
    public BiomeProvider getDefaultBiomeProvider(@NotNull String worldName, @Nullable String id) {
        NaturalGenerator.debug("Biome Provider Called for " + worldName + " using ID: " + id);
        return super.getDefaultBiomeProvider(worldName, id);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        NaturalGenerator.debug("Default World Generator Called for " + worldName + " using ID: " + id);
        if (id == null || id.isEmpty()) id = IrisSettings.get().getGenerator().getDefaultWorldType();
        NaturalGenerator.debug("Generator ID: " + id + " requested by bukkit/plugin");
        IrisDimension dim = loadDimension(worldName, id);
        if (dim == null) {
            throw new RuntimeException("Can't find dimension " + id + "!");
        }

        NaturalGenerator.debug("Assuming IrisDimension: " + dim.getName());

        IrisWorld w = IrisWorld.builder()
                .name(worldName)
                .seed(1337)
                .environment(dim.getEnvironment())
                .worldFolder(new File(Bukkit.getWorldContainer(), worldName))
                .minHeight(dim.getMinHeight())
                .maxHeight(dim.getMaxHeight())
                .build();

        NaturalGenerator.debug("Generator Config: " + w.toString());

        File ff = new File(w.worldFolder(), "naturalworldgen/pack");
        var files = ff.listFiles();
        if (files == null || files.length == 0)
            IO.delete(ff);

        if (!ff.exists()) {
            ff.mkdirs();
            service(StudioSVC.class).installIntoWorld(getSender(), dim.getLoadKey(), w.worldFolder());
        }

        return new BukkitChunkGenerator(w, false, ff, dim.getLoadKey());
    }

    @Nullable
    public static IrisDimension loadDimension(@NonNull String worldName, @NonNull String id) {
        File pack = new File(Bukkit.getWorldContainer(), String.join(File.separator, worldName, "nwg", "pack"));
        var dimension = pack.isDirectory() ? IrisData.get(pack).getDimensionLoader().load(id) : null;
        if (dimension == null) dimension = IrisData.loadAnyDimension(id, null);
        if (dimension == null) {
            NaturalGenerator.warn("Unable to find dimension type " + id + " Looking for online packs...");
            NaturalGenerator.service(StudioSVC.class).downloadSearch(new NaturalDevSender(Bukkit.getConsoleSender()), id, false);
            dimension = IrisData.loadAnyDimension(id, null);

            if (dimension != null) {
                NaturalGenerator.info("Resolved missing dimension, proceeding.");
            }
        }

        return dimension;
    }

    public void splash() {
        NaturalGenerator.info("Server type & version: " + Bukkit.getName() + " v" + Bukkit.getVersion());
        NaturalGenerator.info("Custom Biomes: " + INMS.get().countCustomBiomes());
        printPacks();

        IrisSafeguard.mode().trySplash();
    }

    private void printPacks() {
        File packFolder = NaturalGenerator.service(StudioSVC.class).getWorkspaceFolder();
        File[] packs = packFolder.listFiles(File::isDirectory);
        if (packs == null || packs.length == 0)
            return;
        NaturalGenerator.info("Custom Dimensions: " + packs.length);
        for (File f : packs)
            printPack(f);
    }

    private void printPack(File pack) {
        String dimName = pack.getName();
        String version = "???";
        try (FileReader r = new FileReader(new File(pack, "dimensions/" + dimName + ".json"))) {
            JsonObject json = JsonParser.parseReader(r).getAsJsonObject();
            if (json.has("version"))
                version = json.get("version").getAsString();
        } catch (IOException | JsonParseException ignored) {
        }
        NaturalGenerator.info("  " + dimName + " v" + version);
    }

    public int getIrisVersion() {
        String input = NaturalGenerator.instance.getDescription().getVersion();
        int hyphenIndex = input.indexOf('-');
        if (hyphenIndex != -1) {
            String result = input.substring(0, hyphenIndex);
            result = result.replaceAll("\\.", "");
            return Integer.parseInt(result);
        }
        return -1;
    }

    public int getMCVersion() {
        try {
            String version = Bukkit.getVersion();
            Matcher matcher = Pattern.compile("\\(MC: ([\\d.]+)\\)").matcher(version);
            if (matcher.find()) {
                version = matcher.group(1).replaceAll("\\.", "");
                long versionNumber = Long.parseLong(version);
                if (versionNumber > Integer.MAX_VALUE) {
                    return -1;
                }
                return (int) versionNumber;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
