package com.driussi.kotlinmessenger

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User (
        var username: String? = "",
        var email: String? = "",
        var photoURL: String = "https://firebasestorage.googleapis.com/v0/b/kotlinmessenger-757ff.appspot.com/o/whatsapp.png?alt=media&token=12ec0938-59e9-419b-8134-971fabd4b4b5"
)
