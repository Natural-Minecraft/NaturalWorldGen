package id.naturalsmp.nwg.engine.object.annotations.functions;

import id.naturalsmp.nwg.core.loader.IrisData;
import id.naturalsmp.nwg.core.nms.INMS;
import id.naturalsmp.nwg.engine.framework.ListFunction;
import id.naturalsmp.nwg.toolbelt.collection.KList;

public class StructureKeyOrTagFunction implements ListFunction<KList<String>> {
    @Override
    public String key() {
        return "structure-key-or-tag";
    }

    @Override
    public String fancyName() {
        return "Structure Key or Tag";
    }

    @Override
    public KList<String> apply(IrisData irisData) {
        return INMS.get().getStructureKeys();
    }
}
