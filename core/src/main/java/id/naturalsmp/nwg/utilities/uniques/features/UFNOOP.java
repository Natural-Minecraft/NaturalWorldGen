package id.naturalsmp.nwg.utilities.uniques.features;

import id.naturalsmp.nwg.utilities.math.RNG;
import id.naturalsmp.nwg.utilities.uniques.UFeature;
import id.naturalsmp.nwg.utilities.uniques.UFeatureMeta;
import id.naturalsmp.nwg.utilities.uniques.UImage;

import java.util.function.Consumer;

public class UFNOOP implements UFeature {
    @Override
    public void render(UImage image, RNG rng, double t, Consumer<Double> progressor, UFeatureMeta meta) {
    }
}
