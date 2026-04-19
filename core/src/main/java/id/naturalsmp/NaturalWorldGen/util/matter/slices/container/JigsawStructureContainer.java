package id.naturalsmp.NaturalWorldGen.util.matter.slices.container;

import id.naturalsmp.NaturalWorldGen.engine.object.IrisJigsawStructure;

public class JigsawStructureContainer extends RegistrantContainer<IrisJigsawStructure> {
    public JigsawStructureContainer(String loadKey) {
        super(IrisJigsawStructure.class, loadKey);
    }

    public static JigsawStructureContainer toContainer(IrisJigsawStructure structure) {
        return new JigsawStructureContainer(structure.getLoadKey());
    }
}
