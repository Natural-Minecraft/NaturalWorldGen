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

package id.naturalsmp.nwg.toolbelt.matter.slices;

import id.naturalsmp.nwg.toolbelt.data.B;
import id.naturalsmp.nwg.toolbelt.data.IrisCustomData;
import id.naturalsmp.nwg.toolbelt.data.palette.Palette;
import id.naturalsmp.nwg.toolbelt.matter.Sliced;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Sliced
public class BlockMatter extends RawMatter<BlockData> {
    public static final BlockData AIR = Material.AIR.createBlockData();

    public BlockMatter() {
        this(1, 1, 1);
    }

    public BlockMatter(int width, int height, int depth) {
        super(width, height, depth, BlockData.class);
        registerWriter(World.class, ((w, d, x, y, z) -> {
            if (d instanceof IrisCustomData c)
                w.getBlockAt(x, y, z).setBlockData(c.getBase());
            else w.getBlockAt(x, y, z).setBlockData(d);
        }));
        registerReader(World.class, (w, x, y, z) -> {
            BlockData d = w.getBlockAt(x, y, z).getBlockData();
            return d.getMaterial().isAir() ? null : d;
        });
    }

    @Override
    public Palette<BlockData> getGlobalPalette() {
        return null;
    }

    @Override
    public void writeNode(BlockData b, DataOutputStream dos) throws IOException {
        dos.writeUTF(b.getAsString(true));
    }

    @Override
    public BlockData readNode(DataInputStream din) throws IOException {
        return B.get(din.readUTF());
    }
}
