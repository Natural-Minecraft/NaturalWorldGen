package id.naturalsmp.NaturalWorldGen.util.matter.slices.container;

import id.naturalsmp.NaturalWorldGen.engine.object.IrisJigsawPiece;

public class JigsawPieceContainer extends RegistrantContainer<IrisJigsawPiece> {
    public JigsawPieceContainer(String loadKey) {
        super(IrisJigsawPiece.class, loadKey);
    }

    public static JigsawPieceContainer toContainer(IrisJigsawPiece piece) {
        return new JigsawPieceContainer(piece.getLoadKey());
    }
}
