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

package id.naturalsmp.NaturalWorldGen.engine.object;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.loader.IrisRegistrant;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.engine.mantle.MantleWriter;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.Desc;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.MaxNumber;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.MinNumber;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.RegistryListResource;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import id.naturalsmp.NaturalWorldGen.util.json.JSONObject;
import id.naturalsmp.NaturalWorldGen.util.math.RNG;
import id.naturalsmp.NaturalWorldGen.util.matter.MatterCavern;
import id.naturalsmp.NaturalWorldGen.util.noise.CNG;
import id.naturalsmp.NaturalWorldGen.util.plugin.NaturalDevSender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Desc("Translate objects")
@Data
public class IrisRavine extends IrisRegistrant {
    @Desc("Define the shape of this ravine (2d, ignores Y)")
    private IrisWorm worm = new IrisWorm();

    @RegistryListResource(IrisBiome.class)
    @Desc("Force this cave to only generate the specified custom biome")
    private String customBiome = "";

    @Desc("Define potential forking features")
    private IrisCarving fork = new IrisCarving();

    @Desc("The style used to determine the curvature of this worm's y")
    private IrisShapedGeneratorStyle depthStyle = new IrisShapedGeneratorStyle(NoiseStyle.PERLIN, 5, 18);

    @Desc("The style used to determine the curvature of this worm's y")
    private IrisShapedGeneratorStyle baseWidthStyle = new IrisShapedGeneratorStyle(NoiseStyle.PERLIN, 3, 6);

    @MinNumber(1)
    @MaxNumber(100)
    @Desc("The angle at which the ravine widens as it gets closer to the surface")
    private double angle = 18;

    @MinNumber(1)
    @MaxNumber(100)
    @Desc("The angle at which the ravine widens as it gets closer to the surface")
    private double topAngle = 38;

    @Desc("To fill this cave with lava, set the lava level to a height from the bottom most point of the cave.")
    private int lavaLevel = -1;

    @Desc("How many worm nodes must be placed to actually generate a ravine? Higher reduces the chances but also reduces ravine 'holes'")
    private int nodeThreshold = 5;

    @MinNumber(1)
    @MaxNumber(8)
    @Desc("The thickness of the ravine ribs")
    private double ribThickness = 3;

    @Override
    public String getFolderName() {
        return "ravines";
    }

    @Override
    public String getTypeName() {
        return "Ravine";
    }

    public void generate(MantleWriter writer, RNG rng, Engine engine, int x, int y, int z) {
        generate(writer, rng, new RNG(engine.getSeedManager().getCarve()), engine, x, y, z, 0, -1);
    }

    public void generate(MantleWriter writer, RNG rng, RNG base, Engine engine, int x, int y, int z, int recursion, int waterHint) {
        KList<IrisPosition> pos = getWorm().generate(base.nextParallelRNG(879615), engine.getData(), writer, null, x, y, z, true, 0);
        CNG dg = depthStyle.getGenerator().create(base.nextParallelRNG(7894156), engine.getData());
        CNG bw = baseWidthStyle.getGenerator().create(base.nextParallelRNG(15315456), engine.getData());
        int highestWater = Math.max(waterHint, -1);
        boolean water = false;

        if (highestWater == -1) {
            for (IrisPosition i : pos) {
                int rsurface = y == -1 ? engine.getComplex().getHeightStream().get(x, z).intValue() : y;
                int depth = (int) Math.round(dg.fitDouble(depthStyle.getMin(), depthStyle.getMax(), i.getX(), i.getZ()));
                int surface = (int) Math.round(rsurface - depth * 0.45);
                int yy = surface + depth;
                int th = engine.getHeight(x, z, true);

                if (yy > th && th < engine.getDimension().getFluidHeight()) {
                    highestWater = Math.max(highestWater, yy);
                    water = true;
                    break;
                }
            }
        } else {
            water = true;
        }

        MatterCavern c = new MatterCavern(true, customBiome, (byte) (water ? 1 : 0));
        MatterCavern l = new MatterCavern(true, customBiome, (byte) 2);

        if (pos.size() < nodeThreshold) {
            return;
        }

        for (IrisPosition p : pos) {
            int rsurface = y == -1 ? engine.getComplex().getHeightStream().get(x, z).intValue() : y;
            int depth = (int) Math.round(dg.fitDouble(depthStyle.getMin(), depthStyle.getMax(), p.getX(), p.getZ()));
            int width = (int) Math.round(bw.fitDouble(baseWidthStyle.getMin(), baseWidthStyle.getMax(), p.getX(), p.getZ()));
            int surface = (int) Math.round(rsurface - depth * 0.45);

            fork.doCarving(writer, rng, base, engine, p.getX(), rng.i(surface - depth, surface), p.getZ(), recursion, highestWater);

            for (int i = surface + depth; i >= surface; i--) {
                if (i % ribThickness == 0) {
                    double v = width + ((((surface + depth) - i) * (angle / 360D)));

                    if (v <= 0.25) {
                        break;
                    }

                    if (i <= ribThickness + 2) {
                        break;
                    }

                    if (lavaLevel >= 0 && i <= lavaLevel + (surface - depthStyle.getMid())) {
                        writer.setElipsoid(p.getX(), i, p.getZ(), v, ribThickness, v, true, l);
                    } else {
                        writer.setElipsoid(p.getX(), i, p.getZ(), v, ribThickness, v, true, c);
                    }
                }
            }

            for (int i = surface - depth; i <= surface; i++) {
                if (i % ribThickness == 0) {
                    double v = width - ((((surface - depth) - i) * (angle / 360D)));

                    if (v <= 0.25) {
                        break;
                    }

                    if (i <= ribThickness + 2) {
                        break;
                    }

                    if (lavaLevel >= 0 && i <= lavaLevel + (surface - depthStyle.getMid())) {
                        writer.setElipsoid(p.getX(), i, p.getZ(), v, ribThickness, v, true, l);
                    } else {
                        writer.setElipsoid(p.getX(), i, p.getZ(), v, ribThickness, v, true, c);
                    }
                }
            }
        }
    }

    @Override
    public void scanForErrors(JSONObject p, NaturalDevSender sender) {

    }

    public int getMaxSize(IrisData data, int depth) {
        return getWorm().getMaxDistance() + fork.getMaxRange(data, depth);
    }
}
