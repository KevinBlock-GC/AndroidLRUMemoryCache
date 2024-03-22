package com.example.lrumemcache.cache.managers

import android.os.Parcelable
import android.util.LruCache
import com.example.lrumemcache.cache.CacheEntry
import java.util.concurrent.TimeUnit

class ParcelCacheManager(
    private val maxCacheSizeInBytes: Int,
    private val onEntryRemoved: (evicted: Boolean, key: String?) -> Unit,
    private val onStateUpdated: () -> Unit,
    private val onEntryAdded: (key: String, value: CacheEntry<Any>) -> Unit,
    private val onItemTooLarge: (key: String, value: CacheEntry<Any>) -> Unit

) {
    private val cache: LruCache<String, CacheEntry<Any>> =
        object : LruCache<String, CacheEntry<Any>>(maxCacheSizeInBytes) {
            override fun sizeOf(key: String, value: CacheEntry<Any>): Int {
                return value.sizeInBytes
            }

            override fun entryRemoved(
                evicted: Boolean,
                key: String?,
                oldValue: CacheEntry<Any>?,
                newValue: CacheEntry<Any>?
            ) {
                super.entryRemoved(evicted, key, oldValue, newValue)
                onEntryRemoved(evicted, key)
                onStateUpdated()
            }
        }

    fun put(key: String, value: Parcelable, lifeTime: Long, lifeUnit: TimeUnit) =
        putGeneric(
            key = key,
            value = value,
            lifeTime = lifeTime,
            lifeUnit = lifeUnit
        )

    fun put(key: String, value: List<Parcelable>, lifeTime: Long, lifeUnit: TimeUnit) =
        putGeneric(
            key = key,
            value = value,
            lifeTime = lifeTime,
            lifeUnit = lifeUnit
        )

    private fun putGeneric(key: String, value: Any, lifeTime: Long, lifeUnit: TimeUnit) {
        val cacheEntry: CacheEntry<Any> = CacheEntry(
            value = value,
            lifeInMilliseconds = lifeUnit.toMillis(lifeTime)
        )
        if (cacheEntry.sizeInBytes > maxCacheSizeInBytes) {
            onItemTooLarge(key, cacheEntry)
            return
        }

        cache.put(key, cacheEntry)
        onStateUpdated()
        onEntryAdded(key, cacheEntry)
    }

    fun get(key: String): CacheEntry<Any>? =
        cache.get(key).run {
            if (this != null && isExpired()) {
                remove(key)
                null
            } else {
                this
            }
        }

    fun remove(key: String) =
        cache.remove(key)

    fun clear() =
        cache.evictAll()

    fun size() =
        cache.size()

    fun getAll(): Map<String, CacheEntry<Any>> =
        cache.snapshot()
            .map { it.key.split("$").last() to it.value }
            .toMap()
}