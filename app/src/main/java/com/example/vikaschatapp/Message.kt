package com.example.vikaschatapp

import android.net.Uri
import java.sql.Timestamp

class Message {
    var message: String? = null
    var senderId: String? = null
    var imageUrl: String? = null

    constructor()

    constructor(message: String?, senderId: String?, imageUrl: String?) {
        this.message = message
        this.senderId = senderId
        this.imageUrl = imageUrl
    }
}


