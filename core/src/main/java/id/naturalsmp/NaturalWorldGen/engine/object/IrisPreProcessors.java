package id.naturalsmp.NaturalWorldGen.engine.object;

import id.naturalsmp.NaturalWorldGen.engine.object.annotations.*;
import id.naturalsmp.NaturalWorldGen.engine.object.annotations.functions.ResourceLoadersFunction;
import id.naturalsmp.NaturalWorldGen.util.collection.KList;
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
