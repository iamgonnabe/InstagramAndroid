package com.example.androidproject.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Content (var explain : String? = null,
                    var imageUrl : String? = null,
                    var uid : String? = null,
                    var userId : String? = null,
                    var timestamp : Long? = null,
                    var favoriteCount : Int = 0,
                    var favorites : MutableMap<String,Boolean> = HashMap()) : Parcelable {
    data class Comment (var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp : Long? = null)
}