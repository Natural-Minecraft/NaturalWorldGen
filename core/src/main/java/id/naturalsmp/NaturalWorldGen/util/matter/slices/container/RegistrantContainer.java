package id.naturalsmp.NaturalWorldGen.util.matter.slices.container;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.loader.IrisRegistrant;

public abstract class RegistrantContainer<T extends IrisRegistrant> {
    private final Class<T> type;
    private final String loadKey;

    public RegistrantContainer(Class<T> type, String loadKey) {
        this.type = type;
        this.loadKey = loadKey;
    }

    public T load(IrisData data) {
        return (T) data.getLoaders().get(type).load(loadKey);
    }

    public String getLoadKey() {
        return loadKey;
    }
}
