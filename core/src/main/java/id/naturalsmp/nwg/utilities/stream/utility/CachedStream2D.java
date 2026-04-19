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

package id.naturalsmp.nwg.utilities.stream.utility;

import id.naturalsmp.nwg.NaturalGenerator;
import id.naturalsmp.nwg.core.service.PreservationSVC;
import id.naturalsmp.nwg.engine.framework.Engine;
import id.naturalsmp.nwg.engine.framework.MeteredCache;
import id.naturalsmp.nwg.utilities.cache.WorldCache2D;
import id.naturalsmp.nwg.utilities.data.KCache;
import id.naturalsmp.nwg.utilities.stream.BasicStream;
import id.naturalsmp.nwg.utilities.stream.ProceduralStream;

public class CachedStream2D<T> extends BasicStream<T> implements ProceduralStream<T>, MeteredCache {
    private final ProceduralStream<T> stream;
    private final WorldCache2D<T> cache;
    private final Engine engine;
    private final boolean chunked = true;

    public CachedStream2D(String name, Engine engine, ProceduralStream<T> stream, int size) {
        super();
        this.stream = stream;
        this.engine = engine;
        cache = new WorldCache2D<>(stream::get, size);
        NaturalGenerator.service(PreservationSVC.class).registerCache(this);
    }

    @Override
    public double toDouble(T t) {
        return stream.toDouble(t);
    }

    @Override
    public T fromDouble(double d) {
        return stream.fromDouble(d);
    }

    @Override
    public T get(double x, double z) {
        //return stream.get(x, z);
        return cache.get((int) x, (int) z);
    }

    @Override
    public T get(double x, double y, double z) {
        return stream.get(x, y, z);
    }

    @Override
    public long getSize() {
        return cache.getSize();
    }

    @Override
    public KCache<?, ?> getRawCache() {
        return null;
    }

    @Override
    public long getMaxSize() {
        return cache.getMaxSize();
    }

    @Override
    public boolean isClosed() {
        return engine.isClosed();
    }
}
