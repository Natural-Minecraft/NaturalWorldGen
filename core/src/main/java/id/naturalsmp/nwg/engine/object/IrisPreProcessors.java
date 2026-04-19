package id.naturalsmp.nwg.engine.object;

import id.naturalsmp.nwg.engine.object.annotations.*;
import id.naturalsmp.nwg.engine.object.annotations.functions.ResourceLoadersFunction;
import id.naturalsmp.nwg.toolbelt.collection.KList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Desc("Represents global preprocessors")
public class IrisPreProcessors {
    @Required
    @Desc("The preprocessor type")
    @RegistryListFunction(ResourceLoadersFunction.class)
    private String type = "dimension";

    @Required
    @Desc("The preprocessor scripts\nFile extension: .proc.kts")
    @RegistryListResource(IrisScript.class)
    @ArrayType(type = String.class, min = 1)
    private KList<String> scripts = new KList<>();
}
