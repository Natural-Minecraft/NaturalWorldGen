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

import id.naturalsmp.nwg.engine.object.annotations.Desc;
import id.naturalsmp.nwg.engine.object.annotations.MaxNumber;
import id.naturalsmp.nwg.engine.object.annotations.MinNumber;
import id.naturalsmp.nwg.engine.object.annotations.Required;
import id.naturalsmp.nwg.util.function.NoiseProvider;
import id.naturalsmp.nwg.util.interpolation.InterpolationMethod;
import id.naturalsmp.nwg.util.interpolation.IrisInterpolation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Desc("Configures rotation for naturalworldgen")
@Data
public class IrisInterpolator {
    public static final IrisInterpolator DEFAULT = new IrisInterpolator();

    @Required
    @Desc("The interpolation method when two biomes use different heights but this same generator")
    private InterpolationMethod function = InterpolationMethod.BILINEAR_STARCAST_6;

    @Required
    @MinNumber(1)
    @MaxNumber(8192)
    @Desc("The range checked horizontally. Smaller ranges yeild more detail but are not as smooth.")
    private double horizontalScale = 7;

    @Override
    public int hashCode() {
        return Objects.hash(horizontalScale, function);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IrisInterpolator i) {
            return i.getFunction().equals(function) && i.getHorizontalScale() == horizontalScale;
        }

        return false;
    }

    public double interpolate(double x, double z, NoiseProvider provider) {
        return interpolate((int) Math.round(x), (int) Math.round(z), provider);
    }

    public double interpolate(int x, int z, NoiseProvider provider) {
        return IrisInterpolation.getNoise(getFunction(), x, z, getHorizontalScale(), provider);
    }
}
