package com.crazyidea.alsalah.data.model

import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.google.gson.annotations.SerializedName

data class AzkarResponseApiModel(

    @SerializedName("أذكار الصباح")
    val morning_azkar: List<Azkar>,
    @SerializedName("أذكار المساء")
    val evening_azkar: List<Azkar>,
    @SerializedName("أذكار النوم")
    val sleeping_azkar: List<Azkar>,
    @SerializedName("أذكار الاستيقاظ")
    val wakeup_azkar: List<Azkar>,
    @SerializedName("تسابيح")
    val tasabeh: List<Azkar>,
    @SerializedName("أدعية قرآنية")
    val quran_duaa: List<Azkar>,
    @SerializedName("أدعية الأنبياء")
    val prophets_duaa: List<Azkar>,
    @SerializedName("أذكار بعد السلام من الصلاة المفروضة")
    val afterPrayer_azkar: List<Azkar>
)

