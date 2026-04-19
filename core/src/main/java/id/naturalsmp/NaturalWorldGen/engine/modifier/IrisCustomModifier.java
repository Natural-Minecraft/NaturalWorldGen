package id.naturalsmp.NaturalWorldGen.engine.modifier;
import id.naturalsmp.NaturalWorldGen.core.link.Identifier;
import id.naturalsmp.NaturalWorldGen.engine.framework.Engine;
import id.naturalsmp.NaturalWorldGen.engine.framework.EngineAssignedModifier;
import id.naturalsmp.NaturalWorldGen.util.context.ChunkContext;
import id.naturalsmp.NaturalWorldGen.util.data.IrisCustomData;
import id.naturalsmp.NaturalWorldGen.util.hunk.Hunk;
import id.naturalsmp.NaturalWorldGen.util.mantle.flag.MantleFlag;
import id.naturalsmp.NaturalWorldGen.util.parallel.BurstExecutor;
import id.naturalsmp.NaturalWorldGen.util.parallel.MultiBurst;
import org.bukkit.block.data.BlockData;
public class IrisCustomModifier extends EngineAssignedModifier<BlockData> {
    public IrisCustomModifier(Engine engine) {
        super(engine, "Custom");
    }
    @Override
    public void onModify(int x, int z, Hunk<BlockData> output, boolean multicore, ChunkContext context) {
        var mc = getEngine().getMantle().getMantle().getChunk(x >> 4, z >> 4);
        if (!mc.isFlagged(MantleFlag.CUSTOM_ACTIVE)) return;
        mc.use();

        BurstExecutor burst = MultiBurst.burst.burst(output.getHeight());
        burst.setMulticore(multicore);
        for (int y = 0; y < output.getHeight(); y++) {
            int finalY = y;
            burst.queue(() -> {
                for (int rX = 0; rX < output.getWidth(); rX++) {
                    for (int rZ = 0; rZ < output.getDepth(); rZ++) {
                        BlockData b = output.get(rX, finalY, rZ);
                        if (!(b instanceof IrisCustomData d)) continue;

                        mc.getOrCreate(finalY >> 4)
                                .slice(Identifier.class)
                                .set(rX, finalY & 15, rZ, d.getCustom());
                        output.set(rX, finalY, rZ, d.getBase());
                    }
                }
            });
        }
        burst.complete();
        mc.release();
    }
}