package id.naturalsmp.NaturalWorldGen.core.nms.datapack.v1213;

import id.naturalsmp.NaturalWorldGen.core.nms.datapack.v1206.DataFixerV1206;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisBiomeCustom;
import id.naturalsmp.NaturalWorldGen.util.json.JSONArray;
import id.naturalsmp.NaturalWorldGen.util.json.JSONObject;

public class DataFixerV1213 extends DataFixerV1206 {

    @Override
    public JSONObject fixCustomBiome(IrisBiomeCustom biome, JSONObject json) {
        json = super.fixCustomBiome(biome, json);
        json.put("carvers", new JSONArray());
        return json;
    }
}
