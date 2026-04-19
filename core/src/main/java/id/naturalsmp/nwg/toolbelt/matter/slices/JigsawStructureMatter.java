package id.naturalsmp.nwg.toolbelt.matter.slices;

import id.naturalsmp.nwg.toolbelt.data.palette.Palette;
import id.naturalsmp.nwg.toolbelt.matter.Sliced;
import id.naturalsmp.nwg.toolbelt.matter.slices.container.JigsawStructureContainer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Sliced
public class JigsawStructureMatter extends RawMatter<JigsawStructureContainer> {
    public JigsawStructureMatter() {
        this(1,1,1);
    }

    public JigsawStructureMatter(int width, int height, int depth) {
        super(width, height, depth, JigsawStructureContainer.class);
    }

    @Override
    public Palette<JigsawStructureContainer> getGlobalPalette() {
        return null;
    }

    @Override
    public void writeNode(JigsawStructureContainer b, DataOutputStream dos) throws IOException {
        dos.writeUTF(b.getLoadKey());
    }

    @Override
    public JigsawStructureContainer readNode(DataInputStream din) throws IOException {
        return new JigsawStructureContainer(din.readUTF());
    }
}
