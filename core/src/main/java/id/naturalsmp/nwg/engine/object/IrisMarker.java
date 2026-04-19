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

import id.naturalsmp.nwg.core.loader.IrisRegistrant;
import id.naturalsmp.nwg.engine.object.annotations.ArrayType;
import id.naturalsmp.nwg.engine.object.annotations.Desc;
import id.naturalsmp.nwg.engine.object.annotations.RegistryListResource;
import id.naturalsmp.nwg.utilities.collection.KList;
import id.naturalsmp.nwg.utilities.json.JSONObject;
import id.naturalsmp.nwg.utilities.math.RNG;
import id.naturalsmp.nwg.utilities.plugin.NaturalDevSender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Desc("Represents a marker")
@Data
@EqualsAndHashCode(callSuper = false)
public class IrisMarker extends IrisRegistrant {
    @Desc("A list of spawners to add to anywhere this marker is.")
    @RegistryListResource(IrisSpawner.class)
    @ArrayType(type = String.class, min = 1)
    private KList<String> spawners = new KList<>();

    @Desc("Remove this marker when the block it's assigned to is changed.")
    private boolean removeOnChange = true;

    @Desc("If true, markers will only be placed here if there is 2 air blocks above it.")
    private boolean emptyAbove = true;

    @Desc("If this marker is used, what is the chance it removes itself. For example 25% (0.25) would mean that on average 4 uses will remove a specific marker. Set this below 0 (-1) to never exhaust & set this to 1 or higher to always exhaust on first use.")
    private double exhaustionChance = 0;

    public boolean shouldExhaust() {
        return exhaustionChance > RNG.r.nextDouble();
    }

    @Override
    public String getFolderName() {
        return "markers";
    }

    @Override
    public String getTypeName() {
        return "Marker";
    }

    @Override
    public void scanForErrors(JSONObject p, NaturalDevSender sender) {

    }
}
