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

package id.naturalsmp.nwg.engine.object;

import id.naturalsmp.nwg.core.loader.IrisData;
import id.naturalsmp.nwg.engine.data.cache.AtomicCache;
import id.naturalsmp.nwg.engine.object.annotations.Desc;
import id.naturalsmp.nwg.engine.object.annotations.Required;
import id.naturalsmp.nwg.engine.object.annotations.Snippet;
import id.naturalsmp.nwg.util.collection.KMap;
import id.naturalsmp.nwg.util.math.RNG;
import id.naturalsmp.nwg.util.noise.CNG;
import id.naturalsmp.nwg.util.stream.ProceduralStream;
import lombok.*;
import lombok.experimental.Accessors;

@Snippet("expression-load")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Desc("Represents a variable to use in your expression. Do not set the name to x, y, or z, also don't duplicate names.")
@Data
@EqualsAndHashCode(callSuper = false)
public class IrisExpressionLoad {
    @Required
    @Desc("The variable to assign this value to. Do not set the name to x, y, or z")
    private String name = "";

    @Desc("If the style value is not defined, this value will be used")
    private double staticValue = -1;

    @Desc("If defined, this variable will use a generator style as it's value")
    private IrisGeneratorStyle styleValue = null;

    @Desc("If defined, naturalworldgen will use an internal stream from the engine as it's value")
    private IrisEngineStreamType engineStreamValue = null;

    @Desc("If defined, naturalworldgen will use an internal value from the engine as it's value")
    private IrisEngineValueType engineValue = null;

    private transient AtomicCache<ProceduralStream<Double>> streamCache = new AtomicCache<>();
    private transient AtomicCache<Double> valueCache = new AtomicCache<>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient final KMap<Long, CNG> styleCache = new KMap<>();

    public double getValue(RNG rng, IrisData data, double x, double z) {
        if (engineValue != null) {
            return valueCache.aquire(() -> engineValue.get(data.getEngine()));
        }

        if (engineStreamValue != null) {
            return streamCache.aquire(() -> engineStreamValue.get(data.getEngine())).get(x, z);
        }

        if (styleValue != null) {
            return styleCache.computeIfAbsent(rng.getSeed(), k -> styleValue.createNoCache(new RNG(k), data))
                    .noise(x, z);
        }

        return staticValue;
    }

    public double getValue(RNG rng, IrisData data, double x, double y, double z) {
        if (engineValue != null) {
            return valueCache.aquire(() -> engineValue.get(data.getEngine()));
        }

        if (engineStreamValue != null) {
            return streamCache.aquire(() -> engineStreamValue.get(data.getEngine())).get(x, z);
        }

        if (styleValue != null) {
            return styleCache.computeIfAbsent(rng.getSeed(), k -> styleValue.createNoCache(new RNG(k), data))
                    .noise(x, y, z);
        }

        return staticValue;
    }
}
