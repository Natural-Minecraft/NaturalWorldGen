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

package id.naturalsmp.NaturalWorldGen.engine.platform;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.engine.framework.EngineTarget;
import id.naturalsmp.NaturalWorldGen.engine.framework.Hotloadable;
import id.naturalsmp.NaturalWorldGen.util.data.DataProvider;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface PlatformChunkGenerator extends Hotloadable, DataProvider {
    @Nullable
    Engine getEngine();

    @Override
    default IrisData getData() {
        return getTarget().getData();
    }

    @NotNull
    EngineTarget getTarget();

    void injectChunkReplacement(World world, int x, int z, Executor syncExecutor);

    void close();

    boolean isStudio();

    void touch(World world);

    CompletableFuture<Integer> getSpawnChunks();
}
