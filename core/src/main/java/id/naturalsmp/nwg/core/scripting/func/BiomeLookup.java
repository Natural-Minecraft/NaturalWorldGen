package id.naturalsmp.nwg.core.scripting.func;

import id.naturalsmp.nwg.engine.object.IrisBiome;
import id.naturalsmp.nwg.toolbelt.documentation.BlockCoordinates;

@FunctionalInterface
public interface BiomeLookup {
    @BlockCoordinates
    IrisBiome at(int x, int z);
}
