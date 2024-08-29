package com.example.finalmockserver.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recentbox",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["senderId"]), Index(value = ["receiverId"])]
)
data class RecentBox(
    @PrimaryKey(autoGenerate = true) val recentBoxId: Int = 0,
    val receiverId: Int?,
    val receiverImage: String?,
    val time: String?,
    val name: String?,
    val senderId: Int?,
    val lastestmessage: String?,
    val lastChattingPerson: String?,
    val receiverStatus: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(recentBoxId)
        parcel.writeValue(receiverId)
        parcel.writeString(receiverImage)
        parcel.writeString(time)
        parcel.writeString(name)
        parcel.writeValue(senderId)
        parcel.writeString(lastestmessage)
        parcel.writeString(lastChattingPerson)
        parcel.writeString(receiverStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentBox> {
        override fun createFromParcel(parcel: Parcel): RecentBox {
            return RecentBox(parcel)
        }

        override fun newArray(size: Int): Array<RecentBox?> {
            return arrayOfNulls(size)
        }
    }
}

