package id.naturalsmp.NaturalWorldGen.engine.object;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import org.bukkit.block.data.BlockData;

public interface IObjectLoot {
    KList<IrisBlockData> getFilter();
    KList<BlockData> getFilter(IrisData manager);
    boolean isExact();
    String getName();
    int getWeight();
}
