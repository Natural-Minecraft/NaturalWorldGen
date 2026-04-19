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

package id.naturalsmp.NaturalWorldGen.util.decree.handlers;

import id.naturalsmp.NaturalWorldGen.core.IrisSettings;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisDimension;
import id.naturalsmp.NaturalWorldGen.util.decree.exceptions.DecreeParsingException;
import id.naturalsmp.NaturalWorldGen.util.decree.specialhandlers.RegistrantHandler;

public class DimensionHandler extends RegistrantHandler<IrisDimension> {
    public DimensionHandler() {
        super(IrisDimension.class, false);
    }

    @Override
    public IrisDimension parse(String in, boolean force) throws DecreeParsingException {
        if (in.equalsIgnoreCase("default")) {
            return parse(IrisSettings.get().getGenerator().getDefaultWorldType());
        }
        return super.parse(in, force);
    }

    @Override
    public String getRandomDefault() {
        return "dimension";
    }
}
