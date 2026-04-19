package id.naturalsmp.nwg.util.matter.slices;

import id.naturalsmp.nwg.util.data.palette.Palette;
import id.naturalsmp.nwg.util.matter.Sliced;
import id.naturalsmp.nwg.util.matter.slices.container.JigsawPieceContainer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Sliced
public class JigsawPieceMatter extends RawMatter<JigsawPieceContainer> {
    public JigsawPieceMatter() {
        this(1,1,1);
    }

    public JigsawPieceMatter(int width, int height, int depth) {
        super(width, height, depth, JigsawPieceContainer.class);
    }

    @Override
    public Palette<JigsawPieceContainer> getGlobalPalette() {
        return null;
    }

    @Override
    public void writeNode(JigsawPieceContainer b, DataOutputStream dos) throws IOException {
        dos.writeUTF(b.getLoadKey());
    }

    @Override
    public JigsawPieceContainer readNode(DataInputStream din) throws IOException {
        return new JigsawPieceContainer(din.readUTF());
    }
}
