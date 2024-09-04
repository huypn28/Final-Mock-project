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
            childColumns = ["user1Id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user2Id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user1Id"]), Index(value = ["user2Id"])]
)
data class RecentBox(
    @PrimaryKey(autoGenerate = true) val recentBoxId: Int = 0,
    val user1Id: Int,
    val user2Id: Int,
    val lastMessageId: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(recentBoxId)
        parcel.writeInt(user1Id)
        parcel.writeInt(user2Id)
        parcel.writeInt(lastMessageId)
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