/*
 * NaturalWorldGen is a World Generator for Minecraft Bukkit Servers
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

package id.naturalsmp.NaturalWorldGen.core.tools;

import id.naturalsmp.NaturalWorldGen.NaturalWorldGen;
import id.naturalsmp.NaturalWorldGen.core.IrisSettings;
import id.naturalsmp.NaturalWorldGen.core.gui.PregeneratorJob;
import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.pregenerator.PregenTask;
import id.naturalsmp.NaturalWorldGen.core.pregenerator.PregeneratorMethod;
import id.naturalsmp.NaturalWorldGen.core.pregenerator.methods.CachedPregenMethod;
import id.naturalsmp.NaturalWorldGen.core.pregenerator.methods.HybridPregenMethod;
import id.naturalsmp.NaturalWorldGen.core.service.StudioSVC;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisDimension;
import id.naturalsmp.NaturalWorldGen.engine.platform.PlatformChunkGenerator;
import id.naturalsmp.NaturalWorldGen.util.plugin.NaturalDevSender;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Something you really want to wear if working on NaturalWorldGen. Shit gets pretty hectic down there.
 * Hope you packed snacks & road sodas.
 */
public class IrisToolbelt {
    @ApiStatus.Internal
    public static Map<String, Boolean> toolbeltConfiguration = new HashMap<>();

    /**
     * Will find / download / search for the dimension or return null
     * <p>
     * - You can provide a dimenson in the packs folder by the folder name
     * - You can provide a github repo by using (assumes branch is master unless specified)
     * - GithubUsername/repository
     * - GithubUsername/repository/branch
     *
     * @param dimension the dimension id such as overworld or flat
     * @return the IrisDimension or null
     */
    public static IrisDimension getDimension(String dimension) {
        File pack = NaturalGenerator.instance.getDataFolder("packs", dimension);

        if (!pack.exists()) {
            NaturalWorldGen.service(StudioSVC.class).downloadSearch(new NaturalDevSender(Bukkit.getConsoleSender(), NaturalGenerator.instance.getTag()), dimension, false, false);
        }

        if (!pack.exists()) {
            return null;
        }

        return IrisData.get(pack).getDimensionLoader().load(dimension);
    }

    /**
     * Create a world with plenty of options
     *
     * @return the creator builder
     */
    public static IrisCreator createWorld() {
        return new IrisCreator();
    }

    /**
     * Checks if the given world is an NaturalWorldGen World (same as access(world) != null)
     *
     * @param world the world
     * @return true if it is an NaturalWorldGen Access world
     */
    public static boolean isIrisWorld(World world) {
        if (world == null) {
            return false;
        }

        if (world.getGenerator() instanceof PlatformChunkGenerator f) {
            f.touch(world);
            return true;
        }

        return false;
    }

    public static boolean isIrisStudioWorld(World world) {
        return isIrisWorld(world) && access(world).isStudio();
    }

    /**
     * Get the NaturalWorldGen generator for the given world
     *
     * @param world the given world
     * @return the IrisAccess or null if it's not an NaturalWorldGen World
     */
    public static PlatformChunkGenerator access(World world) {
        if (isIrisWorld(world)) {
            return ((PlatformChunkGenerator) world.getGenerator());
        } /*else {
            NaturalWorldGen.warn("""
                    "---------- No World? ---------------
                    â €â£žâ¢½â¢ªâ¢£â¢£â¢£â¢«â¡ºâ¡µâ£â¡®â£—â¢·â¢½â¢½â¢½â£®â¡·â¡½â£œâ£œâ¢®â¢ºâ£œâ¢·â¢½â¢â¡½â£
                    â ¸â¡¸â œâ •â •â â¢â¢‡â¢â¢½â¢ºâ£ªâ¡³â¡â£Žâ£â¢¯â¢žâ¡¿â£Ÿâ£·â£³â¢¯â¡·â£½â¢½â¢¯â£³â£«â ‡
                    â €â €â¢€â¢€â¢„â¢¬â¢ªâ¡ªâ¡Žâ£†â¡ˆâ šâ œâ •â ‡â —â â¢•â¢¯â¢«â£žâ£¯â£¿â£»â¡½â£â¢—â£—â â €
                    â €â ªâ¡ªâ¡ªâ£ªâ¢ªâ¢ºâ¢¸â¢¢â¢“â¢†â¢¤â¢€â €â €â €â €â ˆâ¢Šâ¢žâ¡¾â£¿â¡¯â£â¢®â ·â â €â €
                    â €â €â €â ˆâ Šâ †â¡ƒâ •â¢•â¢‡â¢‡â¢‡â¢‡â¢‡â¢â¢Žâ¢Žâ¢†â¢„â €â¢‘â£½â£¿â¢â ²â ‰â €â €â €â €
                    â €â €â €â €â €â¡¿â ‚â  â €â¡‡â¢‡â •â¢ˆâ£€â €â â ¡â £â¡£â¡«â£‚â£¿â ¯â¢ªâ °â ‚â €â €â €â €
                    â €â €â €â €â¡¦â¡™â¡‚â¢€â¢¤â¢£â £â¡ˆâ£¾â¡ƒâ  â „â €â¡„â¢±â£Œâ£¶â¢â¢Šâ ‚â €â €â €â €â €â €
                    â €â €â €â €â¢â¡²â£œâ¡®â¡â¢Žâ¢Œâ¢‚â ™â ¢â â¢€â¢˜â¢µâ£½â£¿â¡¿â â â €â €â €â €â €â €â €
                    â €â €â €â €â ¨â£ºâ¡ºâ¡•â¡•â¡±â¡‘â¡†â¡•â¡…â¡•â¡œâ¡¼â¢½â¡»â â €â €â €â €â €â €â €â €â €â €
                    â €â €â €â €â£¼â£³â£«â£¾â£µâ£—â¡µâ¡±â¡¡â¢£â¢‘â¢•â¢œâ¢•â¡â €â €â €â €â €â €â €â €â €â €â €
                    â €â €â €â£´â£¿â£¾â£¿â£¿â£¿â¡¿â¡½â¡‘â¢Œâ ªâ¡¢â¡£â££â¡Ÿâ €â €â €â €â €â €â €â €â €â €â €â €
                    â €â €â €â¡Ÿâ¡¾â£¿â¢¿â¢¿â¢µâ£½â£¾â£¼â£˜â¢¸â¢¸â£žâ¡Ÿâ €â €â €â €â €â €â €â €â €â €â €â €â €
                    â €â €â €â €â â ‡â ¡â ©â¡«â¢¿â£â¡»â¡®â£’â¢½â ‹â €â €â €â €â €â €â €â €â €â €â €â €â €â €
                    """);
        }*/
        return null;
    }

    /**
     * Start a pregenerator task
     *
     * @param task   the scheduled task
     * @param method the method to execute the task
     * @return the pregenerator job (already started)
     */
    public static PregeneratorJob pregenerate(PregenTask task, PregeneratorMethod method, Engine engine) {
        return pregenerate(task, method, engine, IrisSettings.get().getPregen().useCacheByDefault);
    }

    /**
     * Start a pregenerator task
     *
     * @param task   the scheduled task
     * @param method the method to execute the task
     * @return the pregenerator job (already started)
     */
    public static PregeneratorJob pregenerate(PregenTask task, PregeneratorMethod method, Engine engine, boolean cached) {
        return new PregeneratorJob(task, cached && engine != null ? new CachedPregenMethod(method, engine.getWorld().name()) : method, engine);
    }

    /**
     * Start a pregenerator task. If the supplied generator is headless, headless mode is used,
     * otherwise Hybrid mode is used.
     *
     * @param task the scheduled task
     * @param gen  the NaturalWorldGen Generator
     * @return the pregenerator job (already started)
     */
    public static PregeneratorJob pregenerate(PregenTask task, PlatformChunkGenerator gen) {
        return pregenerate(task, new HybridPregenMethod(gen.getEngine().getWorld().realWorld(),
                IrisSettings.getThreadCount(IrisSettings.get().getConcurrency().getParallelism())), gen.getEngine());
    }

    /**
     * Start a pregenerator task. If the supplied generator is headless, headless mode is used,
     * otherwise Hybrid mode is used.
     *
     * @param task  the scheduled task
     * @param world the World
     * @return the pregenerator job (already started)
     */
    public static PregeneratorJob pregenerate(PregenTask task, World world) {
        if (isIrisWorld(world)) {
            return pregenerate(task, access(world));
        }

        return pregenerate(task, new HybridPregenMethod(world, IrisSettings.getThreadCount(IrisSettings.get().getConcurrency().getParallelism())), null);
    }

    /**
     * Evacuate all players from the world into literally any other world.
     * If there are no other worlds, kick them! Not the best but what's mine is mine sometimes...
     *
     * @param world the world to evac
     */
    public static boolean evacuate(World world) {
        for (World i : Bukkit.getWorlds()) {
            if (!i.getName().equals(world.getName())) {
                for (Player j : world.getPlayers()) {
                    new NaturalDevSender(j, NaturalGenerator.instance.getTag()).sendMessage("You have been evacuated from this world.");
                    j.teleport(i.getSpawnLocation());
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Evacuate all players from the world
     *
     * @param world the world to leave
     * @param m     the message
     * @return true if it was evacuated.
     */
    public static boolean evacuate(World world, String m) {
        for (World i : Bukkit.getWorlds()) {
            if (!i.getName().equals(world.getName())) {
                for (Player j : world.getPlayers()) {
                    new NaturalDevSender(j, NaturalGenerator.instance.getTag()).sendMessage("You have been evacuated from this world. " + m);
                    j.teleport(i.getSpawnLocation());
                }
                return true;
            }
        }

        return false;
    }

    public static boolean isStudio(World i) {
        return isIrisWorld(i) && access(i).isStudio();
    }

    public static void retainMantleDataForSlice(String className) {
        toolbeltConfiguration.put("retain.mantle." + className, Boolean.TRUE);
    }

    public static boolean isRetainingMantleDataForSlice(String className) {
        return !toolbeltConfiguration.isEmpty() && toolbeltConfiguration.get("retain.mantle." + className) == Boolean.TRUE;
    }

    public static <T> T getMantleData(World world, int x, int y, int z, Class<T> of) {
        PlatformChunkGenerator e = access(world);
        if (e == null) {
            return null;
        }
        return e.getEngine().getMantle().getMantle().get(x, y - world.getMinHeight(), z, of);
    }

    public static <T> void deleteMantleData(World world, int x, int y, int z, Class<T> of) {
        PlatformChunkGenerator e = access(world);
        if (e == null) {
            return;
        }
        e.getEngine().getMantle().getMantle().remove(x, y - world.getMinHeight(), z, of);
    }

    public static boolean removeWorld(World world) throws IOException {
        return IrisCreator.removeFromBukkitYml(world.getName());
    }
}
