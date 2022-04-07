package org.techtown.secondhand.chatdetail

data class ChatItem(
    val sendId: String,
    val message: String
){
    constructor(): this("","")
}
