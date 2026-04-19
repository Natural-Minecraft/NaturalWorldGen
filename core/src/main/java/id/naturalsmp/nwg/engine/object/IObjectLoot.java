package id.naturalsmp.nwg.engine.object;

import id.naturalsmp.nwg.core.loader.IrisData;
import id.naturalsmp.nwg.utilities.collection.KList;
import org.bukkit.block.data.BlockData;

public interface IObjectLoot {
    KList<IrisBlockData> getFilter();
    KList<BlockData> getFilter(IrisData manager);
    boolean isExact();
    String getName();
    int getWeight();
}
