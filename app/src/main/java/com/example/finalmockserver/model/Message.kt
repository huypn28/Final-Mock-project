package com.example.finalmockserver.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "message",
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
data class Message(
    @PrimaryKey(autoGenerate = true) val messageId: Int = 0,
    val senderId: Int,
    val receiverId: Int,
    val message: String? = "",
    val time: String? = "",
    val deletedByUserId: List<String>? = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(messageId)
        parcel.writeInt(senderId)
        parcel.writeInt(receiverId)
        parcel.writeString(message)
        parcel.writeString(time)
        parcel.writeStringList(deletedByUserId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}