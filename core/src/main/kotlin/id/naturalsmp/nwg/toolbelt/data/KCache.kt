package id.naturalsmp.nwg.toolbelt.data

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.benmanes.caffeine.cache.Scheduler
import id.naturalsmp.nwg.engine.framework.MeteredCache
import id.naturalsmp.nwg.toolbelt.math.RollingSequence
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class KCache<K, V>(private var loader: CacheLoader<K, V>?, private val max: Long, private val fastDump: Boolean = false) :
    MeteredCache {
    private val cache: LoadingCache<K, V> = create(loader)
    private val msu = RollingSequence(100)

    private fun create(loader: CacheLoader<K, V>?): LoadingCache<K, V> {
        return Caffeine
            .newBuilder()
            .maximumSize(max)
            .scheduler(Scheduler.systemScheduler())
            .executor(EXECUTOR)
            .initialCapacity(max.toInt())
            .build { k -> loader?.load(k) }
    }

    fun setLoader(loader: CacheLoader<K, V>) {
        this.loader = loader
    }

    fun invalidate(k: K) {
        cache.invalidate(k)
    }

    fun invalidate() {
        cache.invalidateAll()
    }

    operator fun get(k: K): V? {
        return cache.get(k)
    }

    override fun getSize(): Long {
        return cache.estimatedSize()
    }

    override fun getRawCache(): KCache<*, *> {
        return this
    }

    override fun getMaxSize(): Long {
        return max
    }

    override fun isClosed(): Boolean {
        return false
    }

    fun contains(next: K): Boolean {
        return cache.getIfPresent(next) != null
    }

    companion object {
        @JvmField
        val EXECUTOR: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    }
}
