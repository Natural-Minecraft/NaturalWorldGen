package id.naturalsmp.nwg.core.nms.datapack;

import id.naturalsmp.nwg.engine.object.IrisBiomeCustom;
import id.naturalsmp.nwg.engine.object.IrisDimensionTypeOptions;
import id.naturalsmp.nwg.toolbelt.json.JSONObject;
import org.jetbrains.annotations.Nullable;

public interface IDataFixer {
    default JSONObject fixCustomBiome(IrisBiomeCustom biome, JSONObject json) {
        return json;
    }

    JSONObject resolve(Dimension dimension, @Nullable IrisDimensionTypeOptions options);

    void fixDimension(Dimension dimension, JSONObject json);

    default JSONObject createDimension(Dimension base, int minY, int height, int logicalHeight, @Nullable IrisDimensionTypeOptions options) {
        JSONObject obj = resolve(base, options);
        obj.put("min_y", minY);
        obj.put("height", height);
        obj.put("logical_height", logicalHeight);
        fixDimension(base, obj);
        return obj;
    }

    enum Dimension {
        OVERWORLD,
        NETHER,
        END
    }
}
