package com.example.lrumemcache.cache

import android.os.Parcel
import android.os.Parcelable

data class CacheEntry<T>(
    val value: T,
    private val lifeInMilliseconds: Long,
) {
    val sizeInBytes: Int = calculateObjectSize(value)

    private val expireTime =
        System.currentTimeMillis() + lifeInMilliseconds

    fun isExpired() =
        System.currentTimeMillis() > expireTime

    private fun calculateObjectSize(value: T): Int {
        return when (value) {
            is List<*> -> {
                // Handle a list of Parcelables
                value.sumOf { parcelable ->
                    if (parcelable is Parcelable) {
                        Parcel.obtain().run {
                            parcelable.writeToParcel(this, 0)
                            val bytes = marshall().size
                            recycle()
                            bytes
                        }
                    } else {
                        throw IllegalArgumentException("Unsupported type")
                    }
                }
            }

            is Parcelable -> {
                // Handle a single Parcelable
                Parcel.obtain().run {
                    value.writeToParcel(this, 0)
                    val bytes = marshall().size
                    recycle()
                    bytes
                }
            }

            else -> {
                throw IllegalArgumentException("Unsupported type")
            }
        }
    }
}