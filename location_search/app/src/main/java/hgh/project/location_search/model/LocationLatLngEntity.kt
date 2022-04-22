package hgh.project.location_search.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class LocationLatLngEntity(
    val latitude: Float,
    val longitude: Float
): Parcelable
