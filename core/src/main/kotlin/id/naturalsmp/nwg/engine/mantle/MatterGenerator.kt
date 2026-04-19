package id.naturalsmp.nwg.engine.mantle

import id.naturalsmp.nwg.core.IrisSettings
import id.naturalsmp.nwg.core.nms.container.Pair
import id.naturalsmp.nwg.engine.framework.Engine
import id.naturalsmp.nwg.utilities.context.ChunkContext
import id.naturalsmp.nwg.utilities.documentation.ChunkCoordinates
import id.naturalsmp.nwg.utilities.mantle.Mantle
import id.naturalsmp.nwg.utilities.mantle.flag.MantleFlag
import id.naturalsmp.nwg.utilities.parallel.MultiBurst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

interface MatterGenerator {
    val engine: Engine
    val mantle: Mantle
    val radius: Int
    val realRadius: Int
    val components: List<Pair<List<MantleComponent>, Int>>

    @ChunkCoordinates
    fun generateMatter(x: Int, z: Int, multicore: Boolean, context: ChunkContext) {
        if (!engine.dimension.isUseMantle) return
        val multicore = multicore || IrisSettings.get().generator.isUseMulticoreMantle

        mantle.write(engine.mantle, x, z, radius, multicore).use { writer ->
            for (pair in components) {
                runBlocking {
                    radius(x, z, pair.b) { x, z ->
                        val mc = writer.acquireChunk(x, z)
                        if (mc.isFlagged(MantleFlag.PLANNED))
                            return@radius

                        for (c in pair.a) {
                            if (mc.isFlagged(c.flag))
                                continue

                            launch(multicore) {
                                mc.raiseFlagSuspend(c.flag) {
                                    c.generateLayer(writer, x, z, context)
                                }
                            }
                        }
                    }
                }
            }

            radius(x, z, realRadius) { x, z ->
                writer.acquireChunk(x, z)
                    .flag(MantleFlag.PLANNED, true)
            }
        }
    }

    private inline fun radius(x: Int, z: Int, radius: Int, crossinline task: (Int, Int) -> Unit) {
        for (i in -radius..radius) {
            for (j in -radius..radius) {
                task(x + i, z + j)
            }
        }
    }

    companion object {
        private val dispatcher = MultiBurst.burst.dispatcher//.limitedParallelism(128, "Mantle")
        private fun CoroutineScope.launch(multicore: Boolean, block: suspend CoroutineScope.() -> Unit) =
            launch(if (multicore) dispatcher else EmptyCoroutineContext, block = block)
    }
}