package id.naturalsmp.nwg.core.nms.datapack.v1213;

import id.naturalsmp.nwg.core.nms.datapack.v1206.DataFixerV1206;
import id.naturalsmp.nwg.engine.object.IrisBiomeCustom;
import id.naturalsmp.nwg.toolbelt.json.JSONArray;
import id.naturalsmp.nwg.toolbelt.json.JSONObject;

public class DataFixerV1213 extends DataFixerV1206 {

    @Override
    public JSONObject fixCustomBiome(IrisBiomeCustom biome, JSONObject json) {
        json = super.fixCustomBiome(biome, json);
        json.put("carvers", new JSONArray());
        return json;
    }
}
