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

package id.naturalsmp.nwg.engine.mantle.components;

import id.naturalsmp.nwg.NaturalGenerator;
import id.naturalsmp.nwg.engine.data.cache.Cache;
import id.naturalsmp.nwg.engine.mantle.ComponentFlag;
import id.naturalsmp.nwg.engine.mantle.EngineMantle;
import id.naturalsmp.nwg.engine.mantle.IrisMantleComponent;
import id.naturalsmp.nwg.engine.mantle.MantleWriter;
import id.naturalsmp.nwg.engine.object.*;
import id.naturalsmp.nwg.utilities.collection.KList;
import id.naturalsmp.nwg.utilities.collection.KMap;
import id.naturalsmp.nwg.utilities.collection.KSet;
import id.naturalsmp.nwg.utilities.context.ChunkContext;
import id.naturalsmp.nwg.utilities.data.B;
import id.naturalsmp.nwg.utilities.documentation.BlockCoordinates;
import id.naturalsmp.nwg.utilities.documentation.ChunkCoordinates;
import id.naturalsmp.nwg.utilities.format.Form;
import id.naturalsmp.nwg.utilities.mantle.flag.ReservedFlag;
import id.naturalsmp.nwg.utilities.math.RNG;
import id.naturalsmp.nwg.utilities.matter.MatterStructurePOI;
import id.naturalsmp.nwg.utilities.noise.CNG;
import id.naturalsmp.nwg.utilities.noise.NoiseType;
import id.naturalsmp.nwg.utilities.parallel.BurstExecutor;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@ComponentFlag(ReservedFlag.OBJECT)
public class MantleObjectComponent extends IrisMantleComponent {

    public MantleObjectComponent(EngineMantle engineMantle) {
        super(engineMantle, ReservedFlag.OBJECT, 1);
    }

    @Override
    public void generateLayer(MantleWriter writer, int x, int z, ChunkContext context) {
        RNG rng = applyNoise(x, z, Cache.key(x, z) + seed());
        int xxx = 8 + (x << 4);
        int zzz = 8 + (z << 4);
        IrisRegion region = getComplex().getRegionStream().get(xxx, zzz);
        IrisBiome biome = getComplex().getTrueBiomeStream().get(xxx, zzz);
        placeObjects(writer, rng, x, z, biome, region);
    }

    private RNG applyNoise(int x, int z, long seed) {
        CNG noise = CNG.signatureFast(new RNG(seed), NoiseType.WHITE, NoiseType.GLOB);
        return new RNG((long) (seed * noise.noise(x, z)));
    }

    @ChunkCoordinates
    private void placeObjects(MantleWriter writer, RNG rng, int x, int z, IrisBiome biome, IrisRegion region) {
        for (IrisObjectPlacement i : biome.getSurfaceObjects()) {
            if (rng.chance(i.getChance() + rng.d(-0.005, 0.005))) {
                try {
                    placeObject(writer, rng, x << 4, z << 4, i);
                } catch (Throwable e) {
                    NaturalGenerator.reportError(e);
                    NaturalGenerator.error("Failed to place objects in the following biome: " + biome.getName());
                    NaturalGenerator.error("Object(s) " + i.getPlace().toString(", ") + " (" + e.getClass().getSimpleName() + ").");
                    NaturalGenerator.error("Are these objects missing?");
                    e.printStackTrace();
                }
            }
        }

        for (IrisObjectPlacement i : region.getSurfaceObjects()) {
            if (rng.chance(i.getChance() + rng.d(-0.005, 0.005))) {
                try {
                    placeObject(writer, rng, x << 4, z << 4, i);
                } catch (Throwable e) {
                    NaturalGenerator.reportError(e);
                    NaturalGenerator.error("Failed to place objects in the following region: " + region.getName());
                    NaturalGenerator.error("Object(s) " + i.getPlace().toString(", ") + " (" + e.getClass().getSimpleName() + ").");
                    NaturalGenerator.error("Are these objects missing?");
                    e.printStackTrace();
                }
            }
        }
    }

    @BlockCoordinates
    private void placeObject(MantleWriter writer, RNG rng, int x, int z, IrisObjectPlacement objectPlacement) {
        for (int i = 0; i < objectPlacement.getDensity(rng, x, z, getData()); i++) {
            IrisObject v = objectPlacement.getScale().get(rng, objectPlacement.getObject(getComplex(), rng));
            if (v == null) {
                return;
            }
            int xx = rng.i(x, x + 15);
            int zz = rng.i(z, z + 15);
            int id = rng.i(0, Integer.MAX_VALUE);
            v.place(xx, -1, zz, writer, objectPlacement, rng, (b, data) -> {
                writer.setData(b.getX(), b.getY(), b.getZ(), v.getLoadKey() + "@" + id);
                if (objectPlacement.isDolphinTarget() && objectPlacement.isUnderwater() && B.isStorageChest(data)) {
                    writer.setData(b.getX(), b.getY(), b.getZ(), MatterStructurePOI.BURIED_TREASURE);
                }
            }, null, getData());
        }
    }

    @BlockCoordinates
    private Set<String> guessPlacedKeys(RNG rng, int x, int z, IrisObjectPlacement objectPlacement) {
        Set<String> f = new KSet<>();
        for (int i = 0; i < objectPlacement.getDensity(rng, x, z, getData()); i++) {
            IrisObject v = objectPlacement.getScale().get(rng, objectPlacement.getObject(getComplex(), rng));
            if (v == null) {
                continue;
            }

            f.add(v.getLoadKey());
        }

        return f;
    }

    public Set<String> guess(int x, int z) {
        // todo The guess doesnt bring into account that the placer may return -1
        RNG rng = applyNoise(x, z, Cache.key(x, z) + seed());
        IrisBiome biome = getEngineMantle().getEngine().getSurfaceBiome((x << 4) + 8, (z << 4) + 8);
        IrisRegion region = getEngineMantle().getEngine().getRegion((x << 4) + 8, (z << 4) + 8);
        Set<String> v = new KSet<>();
        for (IrisObjectPlacement i : biome.getSurfaceObjects()) {
            if (rng.chance(i.getChance() + rng.d(-0.005, 0.005))) {
                v.addAll(guessPlacedKeys(rng, x, z, i));
            }
        }

        for (IrisObjectPlacement i : region.getSurfaceObjects()) {
            if (rng.chance(i.getChance() + rng.d(-0.005, 0.005))) {
                v.addAll(guessPlacedKeys(rng, x, z, i));
            }
        }

        return v;
    }

    protected int computeRadius() {
        var dimension = getDimension();

        AtomicInteger xg = new AtomicInteger();
        AtomicInteger zg = new AtomicInteger();

        KSet<String> objects = new KSet<>();
        KMap<IrisObjectScale, KList<String>> scalars = new KMap<>();
        for (var region : dimension.getAllRegions(this::getData)) {
            for (var j : region.getObjects()) {
                if (j.getScale().canScaleBeyond()) {
                    scalars.put(j.getScale(), j.getPlace());
                } else {
                    objects.addAll(j.getPlace());
                }
            }
        }
        for (var biome : dimension.getAllBiomes(this::getData)) {
            for (var j : biome.getObjects()) {
                if (j.getScale().canScaleBeyond()) {
                    scalars.put(j.getScale(), j.getPlace());
                } else {
                    objects.addAll(j.getPlace());
                }
            }
        }

        BurstExecutor e = getEngineMantle().getTarget().getBurster().burst(objects.size());
        KMap<String, BlockVector> sizeCache = new KMap<>();
        for (String i : objects) {
            e.queue(() -> {
                try {
                    BlockVector bv = sizeCache.computeIfAbsent(i, (k) -> {
                        try {
                            return IrisObject.sampleSize(getData().getObjectLoader().findFile(i));
                        } catch (IOException ex) {
                            NaturalGenerator.reportError(ex);
                            ex.printStackTrace();
                        }

                        return null;
                    });

                    if (bv == null) {
                        throw new RuntimeException();
                    }

                    if (Math.max(bv.getBlockX(), bv.getBlockZ()) > 128) {
                        NaturalGenerator.warn("Object " + i + " has a large size (" + bv + ") and may increase memory usage!");
                    }

                    synchronized (xg) {
                        xg.getAndSet(Math.max(bv.getBlockX(), xg.get()));
                    }

                    synchronized (zg) {
                        zg.getAndSet(Math.max(bv.getBlockZ(), zg.get()));
                    }
                } catch (Throwable ed) {
                    NaturalGenerator.reportError(ed);

                }
            });
        }

        for (Map.Entry<IrisObjectScale, KList<String>> entry : scalars.entrySet()) {
            double ms = entry.getKey().getMaximumScale();
            for (String j : entry.getValue()) {
                e.queue(() -> {
                    try {
                        BlockVector bv = sizeCache.computeIfAbsent(j, (k) -> {
                            try {
                                return IrisObject.sampleSize(getData().getObjectLoader().findFile(j));
                            } catch (IOException ioException) {
                                NaturalGenerator.reportError(ioException);
                                ioException.printStackTrace();
                            }

                            return null;
                        });

                        if (bv == null) {
                            throw new RuntimeException();
                        }

                        if (Math.max(bv.getBlockX(), bv.getBlockZ()) > 128) {
                            NaturalGenerator.warn("Object " + j + " has a large size (" + bv + ") and may increase memory usage! (Object scaled up to " + Form.pc(ms, 2) + ")");
                        }

                        synchronized (xg) {
                            xg.getAndSet((int) Math.max(Math.ceil(bv.getBlockX() * ms), xg.get()));
                        }

                        synchronized (zg) {
                            zg.getAndSet((int) Math.max(Math.ceil(bv.getBlockZ() * ms), zg.get()));
                        }
                    } catch (Throwable ee) {
                        NaturalGenerator.reportError(ee);

                    }
                });
            }
        }

        e.complete();
        return Math.max(xg.get(), zg.get());
    }
}
