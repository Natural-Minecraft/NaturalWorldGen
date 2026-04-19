package id.naturalsmp.NaturalWorldGen.engine.framework.placer;

import id.naturalsmp.NaturalWorldGen.NaturalGenerator;
import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.tools.IrisToolbelt;
import id.naturalsmp.NaturalWorldGen.engine.data.cache.Cache;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.core.events.IrisLootEvent;
import id.naturalsmp.NaturalWorldGen.engine.mantle.EngineMantle;
import id.naturalsmp.NaturalWorldGen.engine.object.IObjectPlacer;
import id.naturalsmp.NaturalWorldGen.engine.object.InventorySlotType;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisLootTable;
import id.naturalsmp.NaturalWorldGen.engine.object.TileData;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import id.naturalsmp.NaturalWorldGen.util.data.B;
import id.naturalsmp.NaturalWorldGen.util.data.IrisCustomData;
import id.naturalsmp.NaturalWorldGen.util.math.RNG;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.InventoryHolder;

@Getter
@EqualsAndHashCode(exclude = {"engine", "mantle"})
public class WorldObjectPlacer implements IObjectPlacer {
    private final World world;
    private final Engine engine;
    private final EngineMantle mantle;

    public WorldObjectPlacer(World world) {
        var a = IrisToolbelt.access(world);
        if (a == null || a.getEngine() == null) throw new IllegalStateException(world.getName() + " is not an NaturalGenerator World!");
        this.world = world;
        this.engine = a.getEngine();
        this.mantle = engine.getMantle();
    }

    @Override
    public int getHighest(int x, int z, IrisData data) {
        return mantle.getHighest(x, z, data);
    }

    @Override
    public int getHighest(int x, int z, IrisData data, boolean ignoreFluid) {
        return mantle.getHighest(x, z, data, ignoreFluid);
    }

    @Override
    public void set(int x, int y, int z, BlockData d) {
        Block block = world.getBlockAt(x, y + world.getMinHeight(), z);

        if (y <= world.getMinHeight() || block.getType() == Material.BEDROCK) return;
        InventorySlotType slot = null;
        if (B.isStorageChest(d)) {
            slot = InventorySlotType.STORAGE;
        }

        if (d instanceof IrisCustomData data) {
            block.setBlockData(data.getBase(), false);
            NaturalGenerator.warn("Tried to place custom block at " + x + ", " + y + ", " + z + " which is not supported!");
        } else block.setBlockData(d, false);

        if (slot != null) {
            RNG rx = new RNG(Cache.key(x, z));
            KList<IrisLootTable> tables = engine.getLootTables(rx, block);

            try {
                Bukkit.getPluginManager().callEvent(new IrisLootEvent(engine, block, slot, tables));

                if (!tables.isEmpty()){
                    NaturalGenerator.debug("IrisLootEvent has been accessed");
                }

                if (tables.isEmpty())
                    return;
                InventoryHolder m = (InventoryHolder) block.getState();
                engine.addItems(false, m.getInventory(), rx, tables, slot, world, x, y, z, 15);
            } catch (Throwable e) {
                NaturalGenerator.reportError(e);
            }
        }
    }

    @Override
    public BlockData get(int x, int y, int z) {
        return world.getBlockAt(x, y + world.getMinHeight(), z).getBlockData();
    }

    @Override
    public boolean isPreventingDecay() {
        return mantle.isPreventingDecay();
    }

    @Override
    public boolean isCarved(int x, int y, int z) {
        return mantle.isCarved(x, y, z);
    }

    @Override
    public boolean isSolid(int x, int y, int z) {
        return world.getBlockAt(x, y + world.getMinHeight(), z).getType().isSolid();
    }

    @Override
    public boolean isUnderwater(int x, int z) {
        return mantle.isUnderwater(x, z);
    }

    @Override
    public int getFluidHeight() {
        return mantle.getFluidHeight();
    }

    @Override
    public boolean isDebugSmartBore() {
        return mantle.isDebugSmartBore();
    }

    @Override
    public void setTile(int xx, int yy, int zz, TileData tile) {
        tile.toBukkitTry(world.getBlockAt(xx, yy + world.getMinHeight(), zz));
    }

    @Override
    public <T> void setData(int xx, int yy, int zz, T data) {
    }

    @Override
    public <T> T getData(int xx, int yy, int zz, Class<T> t) {
        return null;
    }
}
