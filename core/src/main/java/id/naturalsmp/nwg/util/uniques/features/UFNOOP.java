package id.naturalsmp.nwg.util.uniques.features;

import id.naturalsmp.nwg.util.math.RNG;
import id.naturalsmp.nwg.util.uniques.UFeature;
import id.naturalsmp.nwg.util.uniques.UFeatureMeta;
import id.naturalsmp.nwg.util.uniques.UImage;

import java.util.function.Consumer;

public class UFNOOP implements UFeature {
    @Override
    public void render(UImage image, RNG rng, double t, Consumer<Double> progressor, UFeatureMeta meta) {
    }
}
