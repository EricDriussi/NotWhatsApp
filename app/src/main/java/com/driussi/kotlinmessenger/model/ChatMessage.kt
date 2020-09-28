package com.driussi.kotlinmessenger.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class ChatMessage(
        var id: String? = "",
        var text: String? = "",
        var fromID: String? = "",
        var toID: String? = "",
        var timestamp: Long?
) : Parcelable{
    constructor(): this("", "", "", "", -1)
}
