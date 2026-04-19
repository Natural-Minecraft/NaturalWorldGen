package id.naturalsmp.NaturalWorldGen.engine.object.matter;

import id.naturalsmp.NaturalWorldGen.core.loader.IrisRegistrant;
import id.naturalsmp.NaturalWorldGen.engine.object.IrisObject;
import id.naturalsmp.NaturalWorldGen.util.json.JSONObject;
import id.naturalsmp.NaturalWorldGen.util.matter.IrisMatter;
import id.naturalsmp.NaturalWorldGen.util.matter.Matter;
import id.naturalsmp.NaturalWorldGen.util.plugin.NaturalDevSender;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.IOException;

@Data
@EqualsAndHashCode(callSuper = false)
public class IrisMatterObject extends IrisRegistrant {
    private final Matter matter;

    public IrisMatterObject() {
        this(1, 1, 1);
    }

    public IrisMatterObject(int w, int h, int d) {
        this(new IrisMatter(w, h, d));
    }

    public IrisMatterObject(Matter matter) {
        this.matter = matter;
    }

    public static IrisMatterObject from(IrisObject object) {
        return new IrisMatterObject(Matter.from(object));
    }

    public static IrisMatterObject from(File j) throws IOException, ClassNotFoundException {
        return new IrisMatterObject(Matter.read(j));
    }

    @Override
    public String getFolderName() {
        return "matter";
    }

    @Override
    public String getTypeName() {
        return "Matter";
    }

    @Override
    public void scanForErrors(JSONObject p, NaturalDevSender sender) {

    }
}
