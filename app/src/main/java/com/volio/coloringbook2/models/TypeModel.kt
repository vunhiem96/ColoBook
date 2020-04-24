package com.volio.coloringbook2.models

import android.os.Parcel
import android.os.Parcelable

class TypeModel(val name: String,val id: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TypeModel> {
        override fun createFromParcel(parcel: Parcel): TypeModel {
            return TypeModel(parcel)
        }

        override fun newArray(size: Int): Array<TypeModel?> {
            return arrayOfNulls(size)
        }
    }
}
