package org.techtown.secondhand.home

data class ArticleModel(
    val sellerId: String,
    val title:String,
    val creatdAt:Long,
    val price: String,
    val imageUrl: String
){
    constructor(): this("","",0,"","")
}
