package com.example.lrumemcache.cache.keys

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ParcelableKey<T : Any> {
    abstract val resultClass: Class<T>

    data object PlaceKey : ParcelableKey<Place>() {
        override val resultClass: Class<Place> = Place::class.java
    }

    data object PhoneNumberKey : ParcelableKey<PhoneNumber>() {
        override val resultClass: Class<PhoneNumber> = PhoneNumber::class.java
    }

    data object ListOfPlaces : ParcelableKey<PlaceList>() {
        override val resultClass: Class<PlaceList> = PlaceList::class.java
    }

    data object ListOfCityPhoneNumbers : ParcelableKey<PhoneNumberList>() {
        override val resultClass: Class<PhoneNumberList> = PhoneNumberList::class.java
    }

    data object ListOfCountryPhoneNumbers : ParcelableKey<PhoneNumberList>() {
        override val resultClass: Class<PhoneNumberList> = PhoneNumberList::class.java
    }
}

@Parcelize
data class PhoneNumber(val name: String, val number: String) : Parcelable

@Parcelize
data class Place(val city: String, val lat: Double, val long: Double) : Parcelable

@Parcelize
data class PhoneNumberList(val phoneNumbers: List<PhoneNumber>) : Parcelable

@Parcelize
data class PlaceList(val places: List<Place>) : Parcelable

data class ThisWontWork(val ohNo: Boolean)
