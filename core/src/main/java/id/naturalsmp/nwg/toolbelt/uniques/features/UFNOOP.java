package id.naturalsmp.nwg.toolbelt.uniques.features;

import id.naturalsmp.nwg.toolbelt.math.RNG;
import id.naturalsmp.nwg.toolbelt.uniques.UFeature;
import id.naturalsmp.nwg.toolbelt.uniques.UFeatureMeta;
import id.naturalsmp.nwg.toolbelt.uniques.UImage;

import java.util.function.Consumer;

public class UFNOOP implements UFeature {
    @Override
    public void render(UImage image, RNG rng, double t, Consumer<Double> progressor, UFeatureMeta meta) {
    }
}
