package com.example.lrumemcache.cache

import android.os.Parcelable
import com.example.lrumemcache.cache.keys.ParcelableKey
import com.example.lrumemcache.cache.managers.ParcelCacheManager
import java.util.concurrent.TimeUnit


class ParcelableCacheHelper(private val cacheManager: ParcelCacheManager) {
    fun <T : Parcelable> get(parcelKey: ParcelableKey<T>): ParcelableCacheResultBuilder<T> {
        return ParcelableCacheResultBuilder(parcelKey, cacheManager)
    }
}

data class ParcelableCacheResultBuilder<T : Parcelable>(
    internal val parcelKey: ParcelableKey<T>,
    internal val cacheManager: ParcelCacheManager
) {
    private var expiresInMilliseconds: Long = 1000 * 30 * 60 // Default 30 minutes

    fun expiresIn(time: Long, timeUnit: TimeUnit): ParcelableCacheResultBuilder<T> {
        expiresInMilliseconds = timeUnit.toMillis(time)
        return this
    }

    fun result(): T? {
        val cachedResult = cacheManager.get(parcelKey::class.java.name)
        if (cachedResult != null && parcelKey.resultClass.isInstance(cachedResult.value)) {
            val castedResult = parcelKey.resultClass.cast(cachedResult.value)
            if (castedResult != null) {
                println("Returning cached result for ${parcelKey::class.java.name}")
                return castedResult
            }
        }

        return null
    }

    fun cacheResult(force: Boolean = false, resultBuilder: () -> T): T {
        val cachedResult = result()

        if (cachedResult != null && !force) {
            return cachedResult
        }

        println("Caching new result for ${parcelKey::class.java.name}")
        val newResult = resultBuilder()
        cacheManager.put(
            key = parcelKey::class.java.name,
            value = newResult,
            lifeTime = expiresInMilliseconds,
            lifeUnit = TimeUnit.MILLISECONDS
        )
        return newResult
    }
}