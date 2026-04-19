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

package id.naturalsmp.nwg.utilities.decree.handlers;

import id.naturalsmp.nwg.utilities.collection.KList;
import id.naturalsmp.nwg.utilities.decree.DecreeParameterHandler;
import id.naturalsmp.nwg.utilities.decree.exceptions.DecreeParsingException;
import id.naturalsmp.nwg.utilities.math.RNG;

import java.util.concurrent.atomic.AtomicReference;

public class IntegerHandler implements DecreeParameterHandler<Integer> {
    @Override
    public KList<Integer> getPossibilities() {
        return null;
    }

    @Override
    public Integer parse(String in, boolean force) throws DecreeParsingException {
        try {
            AtomicReference<String> r = new AtomicReference<>(in);
            double m = getMultiplier(r);
            return (int) (Integer.valueOf(r.get()).doubleValue() * m);
        } catch (Throwable e) {
            throw new DecreeParsingException("Unable to parse integer \"" + in + "\"");
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(Integer.class) || type.equals(int.class);
    }

    @Override
    public String toString(Integer f) {
        return f.toString();
    }

    @Override
    public String getRandomDefault() {
        return RNG.r.i(0, 99) + "";
    }
}
