package id.naturalsmp.nwg.toolbelt.matter.slices.container;

import id.naturalsmp.nwg.engine.object.IrisJigsawPiece;

public class JigsawPieceContainer extends RegistrantContainer<IrisJigsawPiece> {
    public JigsawPieceContainer(String loadKey) {
        super(IrisJigsawPiece.class, loadKey);
    }

    public static JigsawPieceContainer toContainer(IrisJigsawPiece piece) {
        return new JigsawPieceContainer(piece.getLoadKey());
    }
}
