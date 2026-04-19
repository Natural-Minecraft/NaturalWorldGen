package id.naturalsmp.nwg.engine.object.annotations.functions;

import id.naturalsmp.nwg.core.loader.IrisData;
import id.naturalsmp.nwg.core.nms.INMS;
import id.naturalsmp.nwg.engine.framework.ListFunction;
import id.naturalsmp.nwg.utilities.collection.KList;

public class StructureKeyFunction implements ListFunction<KList<String>> {
    @Override
    public String key() {
        return "structure-key";
    }

    @Override
    public String fancyName() {
        return "Structure Key";
    }

    @Override
    public KList<String> apply(IrisData irisData) {
        return INMS.get().getStructureKeys().removeWhere(t -> t.startsWith("#"));
    }
}
