package com.driussi.kotlinmessenger.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class User(
        var uid: String = "",
        var username: String = "",
        var email: String? = "",
        var photoURL: String = "https://firebasestorage.googleapis.com/v0/b/kotlinmessenger-757ff.appspot.com/o/whatsapp.png?alt=media&token=12ec0938-59e9-419b-8134-971fabd4b4b5"
) : Parcelable