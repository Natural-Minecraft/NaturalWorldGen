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

package id.naturalsmp.NaturalWorldGen.engine.object;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisRegistrant;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.ArrayType;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.Desc;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.RegistryListResource;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.Required;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import id.naturalsmp.NaturalWorldGen.util.json.JSONObject;
import id.naturalsmp.NaturalWorldGen.util.plugin.NaturalDevSender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor

@Desc("Represents a structure piece pool")
@Data
@EqualsAndHashCode(callSuper = false)
public class IrisJigsawPool extends IrisRegistrant {
    @RegistryListResource(IrisJigsawPiece.class)
    @Required
    @ArrayType(min = 1, type = String.class)
    @Desc("A list of structure piece pools")
    private KList<String> pieces = new KList<>();

    @Override
    public String getFolderName() {
        return "jigsaw-pools";
    }

    @Override
    public String getTypeName() {
        return "Jigsaw Pool";
    }

    @Override
    public void scanForErrors(JSONObject p, NaturalDevSender sender) {

    }
}
