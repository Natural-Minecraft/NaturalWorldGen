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

package id.naturalsmp.nwg.engine.framework;

import id.naturalsmp.nwg.engine.IrisComplex;
import id.naturalsmp.nwg.engine.mantle.EngineMantle;
import id.naturalsmp.nwg.utilities.context.ChunkContext;
import id.naturalsmp.nwg.utilities.context.IrisContext;
import id.naturalsmp.nwg.utilities.documentation.BlockCoordinates;
import id.naturalsmp.nwg.utilities.hunk.Hunk;
import id.naturalsmp.nwg.utilities.math.RollingSequence;
import id.naturalsmp.nwg.utilities.parallel.BurstExecutor;
import id.naturalsmp.nwg.utilities.parallel.MultiBurst;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;

public interface EngineMode extends Staged {
    RollingSequence r = new RollingSequence(64);
    RollingSequence r2 = new RollingSequence(256);

    void close();

    Engine getEngine();

    default MultiBurst burst() {
        return getEngine().burst();
    }

    default EngineStage burst(EngineStage... stages) {
        return (x, z, blocks, biomes, multicore, ctx) -> {
            BurstExecutor e = burst().burst(stages.length);
            e.setMulticore(multicore);

            for (EngineStage i : stages) {
                e.queue(() -> i.generate(x, z, blocks, biomes, multicore, ctx));
            }

            e.complete();
        };
    }

    default IrisComplex getComplex() {
        return getEngine().getComplex();
    }

    default EngineMantle getMantle() {
        return getEngine().getMantle();
    }

    default void generateMatter(int x, int z, boolean multicore, ChunkContext context) {
        getMantle().generateMatter(x, z, multicore, context);
    }

    @BlockCoordinates
    default void generate(int x, int z, Hunk<BlockData> blocks, Hunk<Biome> biomes, boolean multicore) {
        ChunkContext ctx = new ChunkContext(x, z, getComplex());
        IrisContext.getOr(getEngine()).setChunkContext(ctx);

        for (EngineStage i : getStages()) {
            i.generate(x, z, blocks, biomes, multicore, ctx);
        }
    }
}
