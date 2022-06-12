package com.crazyidea.alsalah.data.model

import java.io.Serializable

data class Articles(
    val id: Int,
    val title: String,
    val text: String,
    val image: String,
    val views: Int,
    val share: Int,
    val comments: Int,
    var likes: Int,
    var liked: Boolean,
):Serializable

