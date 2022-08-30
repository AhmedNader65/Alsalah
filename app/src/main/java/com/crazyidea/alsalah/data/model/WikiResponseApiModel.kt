package com.crazyidea.alsalah.data.model

import com.google.gson.annotations.SerializedName

data class WikiResponseApiModel(
    @SerializedName("parse")
    val parse: parseModel,
)

data class parseModel(
    @SerializedName("text")
    val wikitext: WikiTextModel,
)

data class WikiTextModel(
    @SerializedName("*")
    val text: String,
)
