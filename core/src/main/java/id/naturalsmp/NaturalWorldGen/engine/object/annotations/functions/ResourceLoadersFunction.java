package id.naturalsmp.NaturalWorldGen.engine.object.annotations.functions;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisData;
import id.naturalsmp.NaturalWorldGen.core.loader.ResourceLoader;
import id.naturalsmp.NaturalWorldGen.engine.framework.ListFunction;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;

public class ResourceLoadersFunction implements ListFunction<KList<String>> {
    @Override
    public String key() {
        return "resource-loader";
    }

    @Override
    public String fancyName() {
        return "Resource Loader";
    }

    @Override
    public KList<String> apply(IrisData data) {
        return data.getLoaders()
                .values()
                .stream()
                .filter(rl -> ResourceLoader.class.equals(rl.getClass()))
                .map(ResourceLoader::getFolderName)
                .collect(KList.collector());
    }
}
