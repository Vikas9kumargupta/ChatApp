package com.example.vikaschatapp

class Message {
    var message: String? = null
    var senderId: String? = null

    constructor() {

    }

    constructor(message: String?, senderUid: String?) {
        this.message = message
        this.senderId = senderUid
    }
}