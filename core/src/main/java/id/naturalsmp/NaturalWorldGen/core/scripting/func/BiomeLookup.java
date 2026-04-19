package id.naturalsmp.NaturalWorldGen.core.scripting.func;

import id.naturalsmp.NaturalWorldGen.engine.object.IrisBiome;
import id.naturalsmp.NaturalWorldGen.util.documentation.BlockCoordinates;

@FunctionalInterface
public interface BiomeLookup {
    @BlockCoordinates
    IrisBiome at(int x, int z);
}
