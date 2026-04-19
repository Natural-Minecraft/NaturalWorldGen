package id.naturalsmp.NaturalWorldGen.util.decree.handlers;

import id.naturalsmp.NaturalWorldGen.core.nms.datapack.DataVersion;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
import id.naturalsmp.NaturalWorldGen.util.decree.DecreeParameterHandler;
import id.naturalsmp.NaturalWorldGen.util.decree.exceptions.DecreeParsingException;

public class DataVersionHandler implements DecreeParameterHandler<DataVersion> {
    @Override
    public KList<DataVersion> getPossibilities() {
        return new KList<>(DataVersion.values()).qdel(DataVersion.UNSUPPORTED);
    }

    @Override
    public String toString(DataVersion version) {
        return version.getVersion();
    }

    @Override
    public DataVersion parse(String in, boolean force) throws DecreeParsingException {
        if (in.equalsIgnoreCase("latest")) {
            return DataVersion.getLatest();
        }
        for (DataVersion v : DataVersion.values()) {
            if (v.getVersion().equalsIgnoreCase(in)) {
                return v;
            }
        }
        throw new DecreeParsingException("Unable to parse data version \"" + in + "\"");
    }

    @Override
    public boolean supports(Class<?> type) {
        return DataVersion.class.equals(type);
    }
}
