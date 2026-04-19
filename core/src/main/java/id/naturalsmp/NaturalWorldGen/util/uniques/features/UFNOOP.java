package id.naturalsmp.NaturalWorldGen.util.uniques.features;

import id.naturalsmp.NaturalWorldGen.util.math.RNG;
import id.naturalsmp.NaturalWorldGen.util.uniques.UFeature;
import id.naturalsmp.NaturalWorldGen.util.uniques.UFeatureMeta;
import id.naturalsmp.NaturalWorldGen.util.uniques.UImage;

import java.util.function.Consumer;

public class UFNOOP implements UFeature {
    @Override
    public void render(UImage image, RNG rng, double t, Consumer<Double> progressor, UFeatureMeta meta) {
    }
}
