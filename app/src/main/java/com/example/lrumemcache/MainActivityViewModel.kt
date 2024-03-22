package com.example.lrumemcache

import androidx.lifecycle.ViewModel
import com.example.lrumemcache.cache.CacheEntry
import com.example.lrumemcache.cache.ParcelableCacheHelper
import com.example.lrumemcache.cache.keys.ParcelableKey
import com.example.lrumemcache.cache.keys.PhoneNumber
import com.example.lrumemcache.cache.keys.PhoneNumberList
import com.example.lrumemcache.cache.keys.Place
import com.example.lrumemcache.cache.keys.PlaceList
import com.example.lrumemcache.cache.managers.ParcelCacheManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    private val memorySizeInBytes = 1024 * 1024 * 4 // 4MB
    private val cacheManager: ParcelCacheManager = ParcelCacheManager(
        maxCacheSizeInBytes = memorySizeInBytes, // 4MB
        onEntryRemoved = { evicted, key ->
            if (evicted) {
                println("Entry with key $key was evicted")
            } else {
                println("Entry with key $key was removed")
            }
        },
        onStateUpdated = {
            onStateUpdated()
        },
        onEntryAdded = { key, value ->
            println("Entry with key $key was added")
        },
        onItemTooLarge = { key, value ->
            println("Entry with key $key was too large to be stored")
        }
    )
    private val parcelableCacheHelper: ParcelableCacheHelper = ParcelableCacheHelper(cacheManager)
    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state = _state

    private fun getMemoryPercentageUsed(): Double {
        val usedMemory = cacheManager.size()
        return (usedMemory.toDouble() / memorySizeInBytes) * 100.0
    }

    data class State(
        val memoryPercentageUsed: Double = 0.0,
        val allItems: Map<String, CacheEntry<Any>> = emptyMap()
    )

    fun putSmallItem() {
        parcelableCacheHelper.get(ParcelableKey.PhoneNumberKey)
            .expiresIn(30, TimeUnit.MINUTES)
            .cacheResult {
                PhoneNumber("John Doe", "123-456-7890")
            }
    }

    fun putMediumItem() {
        parcelableCacheHelper.get(ParcelableKey.PlaceKey)
            .expiresIn(5, TimeUnit.MINUTES)
            .cacheResult {
                Place(("Riverhead").repeat(10000), 10.20, 3.00)
            }
    }

    fun putHeftyItem() {
        parcelableCacheHelper.get(ParcelableKey.ListOfPlaces)
            .expiresIn(5, TimeUnit.MINUTES)
            .cacheResult {
                val items = (0..9).map {
                    Place(("Riverhead").repeat(10000), 10.20, 3.00)
                }
                PlaceList(items)
            }
    }

    fun putLargeItem() {
        parcelableCacheHelper.get(ParcelableKey.ListOfCityPhoneNumbers)
            .expiresIn(5, TimeUnit.MINUTES)
            .cacheResult {
                val items = (1..25000).map {
                    PhoneNumber("John Doe", ("123-456-7890").repeat(5))
                }
                PhoneNumberList(items)
            }
    }

    fun putExtraLargeItem() {
        parcelableCacheHelper.get(ParcelableKey.ListOfCountryPhoneNumbers)
            .expiresIn(5, TimeUnit.MINUTES)
            .cacheResult {
                val items = (1..50000).map {
                    PhoneNumber("John Doe", ("123-456-7890").repeat(5))
                }
                PhoneNumberList(items)
            }
    }

    fun clearCache() {
        cacheManager.clear()
    }

    private fun onStateUpdated() {
        _state.update {
            it.copy(
                allItems = cacheManager.getAll(),
                memoryPercentageUsed = getMemoryPercentageUsed()
            )
        }
    }
}