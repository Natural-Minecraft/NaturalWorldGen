package id.naturalsmp.nwg.engine.object.annotations.functions;

import id.naturalsmp.nwg.NaturalGenerator;
import id.naturalsmp.nwg.core.loader.IrisData;
import id.naturalsmp.nwg.engine.framework.ListFunction;
import id.naturalsmp.nwg.engine.mantle.ComponentFlag;
import id.naturalsmp.nwg.engine.mantle.MantleComponent;
import id.naturalsmp.nwg.utilities.collection.KList;
import id.naturalsmp.nwg.utilities.mantle.flag.MantleFlag;

import java.util.Objects;

public class ComponentFlagFunction implements ListFunction<KList<String>> {
    @Override
    public String key() {
        return "component-flag";
    }

    @Override
    public String fancyName() {
        return "Component Flag";
    }

    @Override
    public KList<String> apply(IrisData data) {
        var engine = data.getEngine();
        if (engine != null) return engine.getMantle().getComponentFlags().toStringList();
        return NaturalGenerator.getClasses("id.naturalsmp.nwg.engine.mantle.components", ComponentFlag.class)
                .stream()
                .filter(MantleComponent.class::isAssignableFrom)
                .map(c -> c.getDeclaredAnnotation(ComponentFlag.class))
                .filter(Objects::nonNull)
                .map(ComponentFlag::value)
                .map(MantleFlag::toString)
                .collect(KList.collector());
    }
}
