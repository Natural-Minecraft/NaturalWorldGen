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

package id.naturalsmp.NaturalWorldGen.util.decree;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.tools.IrisToolbelt;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.engine.platform.PlatformChunkGenerator;
import id.naturalsmp.NaturalWorldGen.util.plugin.NaturalDevSender;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface DecreeExecutor {
    default NaturalDevSender sender() {
        return DecreeContext.get();
    }

    default Player player() {
        return sender().player();
    }

    default IrisData data() {
        var access = access();
        if (access != null) {
            return access.getData();
        }
        return null;
    }

    default Engine engine() {
        if (sender().isPlayer() && IrisToolbelt.access(sender().player().getWorld()) != null) {
            PlatformChunkGenerator gen = IrisToolbelt.access(sender().player().getWorld());
            if (gen != null) {
                return gen.getEngine();
            }
        }

        return null;
    }

    default PlatformChunkGenerator access() {
        if (sender().isPlayer()) {
            return IrisToolbelt.access(world());
        }
        return null;
    }

    default World world() {
        if (sender().isPlayer()) {
            return sender().player().getWorld();
        }
        return null;
    }

    default <T> T get(T v, T ifUndefined) {
        return v == null ? ifUndefined : v;
    }
}
