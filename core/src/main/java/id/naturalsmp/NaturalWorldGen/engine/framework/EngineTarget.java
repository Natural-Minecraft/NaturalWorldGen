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

package id.naturalsmp.NaturalWorldGen.engine.framework;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisDimension;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisWorld;
import id.naturalsmp.NaturalWorldGen.util.parallel.MultiBurst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = "data")
@ToString(exclude = "data")
public class EngineTarget {
    private final MultiBurst burster;
    private final IrisData data;
    private IrisDimension dimension;
    private IrisWorld world;

    public EngineTarget(IrisWorld world, IrisDimension dimension, IrisData data) {
        this.world = world;
        this.dimension = dimension;
        this.data = data;
        this.burster = MultiBurst.burst;
    }

    public int getHeight() {
        return world.maxHeight() - world.minHeight();
    }

    public void close() {

    }
}
