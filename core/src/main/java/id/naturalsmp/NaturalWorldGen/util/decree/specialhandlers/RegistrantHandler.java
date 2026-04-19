package id.naturalsmp.NaturalWorldGen.util.decree.specialhandlers;

import id.naturalsmp.NaturalWorldGen.NaturalGenerator;
import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.loader.IrisRegistrant;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import id.naturalsmp.NaturalWorldGen.util.decree.DecreeParameterHandler;
import id.naturalsmp.NaturalWorldGen.util.decree.exceptions.DecreeParsingException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public abstract class RegistrantHandler<T extends IrisRegistrant> implements DecreeParameterHandler<T> {
    private final Class<T> type;
    private final String name;
    private final boolean nullable;

    public RegistrantHandler(Class<T> type, boolean nullable) {
        this.type = type;
        this.name = type.getSimpleName().replaceFirst("NaturalWorldGen", "");
        this.nullable = nullable;
    }

    @Override
    public KList<T> getPossibilities() {
        KList<T> p = new KList<>();
        Set<String> known = new HashSet<>();
        IrisData data = data();
        if (data != null) {
            for (T j : data.getLoader(type).loadAll(data.getLoader(type).getPossibleKeys())) {
                known.add(j.getLoadKey());
                p.add(j);
            }
        }

        //noinspection ConstantConditions
        for (File i : NaturalGenerator.instance.getDataFolder("packs").listFiles()) {
            if (i.isDirectory()) {
                data = IrisData.get(i);
                for (T j : data.getLoader(type).loadAll(data.getLoader(type).getPossibleKeys())) {
                    if (known.add(j.getLoadKey()))
                        p.add(j);
                }
            }
        }

        return p;
    }

    @Override
    public String toString(T t) {
        return t != null ? t.getLoadKey() : "null";
    }

    @Override
    public T parse(String in, boolean force) throws DecreeParsingException {
        if (in.equals("null") && nullable) {
            return null;
        }
        KList<T> options = getPossibilities(in);
        if (options.isEmpty()) {
            throw new DecreeParsingException("Unable to find " + name + " \"" + in + "\"");
        }

        return options.stream()
                .filter((i) -> toString(i).equalsIgnoreCase(in))
                .findFirst()
                .orElseThrow(() -> new DecreeParsingException("Unable to filter which " + name + " \"" + in + "\""));
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(this.type);
    }
}
