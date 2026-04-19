package id.naturalsmp.NaturalWorldGen.engine.object.annotations.functions;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.nms.INMS;
import id.naturalsmp.NaturalWorldGen.engine.framework.ListFunction;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;

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
