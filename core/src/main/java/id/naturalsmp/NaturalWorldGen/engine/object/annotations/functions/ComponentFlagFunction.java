package id.naturalsmp.NaturalWorldGen.engine.object.annotations.functions;

import id.naturalsmp.NaturalWorldGen.NaturalGenerator;
import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.engine.framework.ListFunction;
import id.naturalsmp.NaturalWorldGen.engine.mantle.ComponentFlag;
import id.naturalsmp.NaturalWorldGen.engine.mantle.MantleComponent;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import id.naturalsmp.NaturalWorldGen.util.mantle.flag.MantleFlag;

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
        return NaturalWorldGen.getClasses("id.naturalsmp.NaturalWorldGen.engine.mantle.components", ComponentFlag.class)
                .stream()
                .filter(MantleComponent.class::isAssignableFrom)
                .map(c -> c.getDeclaredAnnotation(ComponentFlag.class))
                .filter(Objects::nonNull)
                .map(ComponentFlag::value)
                .map(MantleFlag::toString)
                .collect(KList.collector());
    }
}
