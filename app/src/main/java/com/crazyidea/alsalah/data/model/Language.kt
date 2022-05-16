package com.crazyidea.alsalah.data.model

import android.annotation.SuppressLint
import android.content.Context
import com.crazyidea.alsalah.spacedComma
import io.github.cosinekitty.astronomy.EclipseKind
import java.util.*

enum class Language(val code: String, val nativeName: String) {
    // The following order is used for language change dialog also
    // Official languages
    FA("fa", "فارسی"),
    FA_AF("fa-AF", "دری"),
    PS("ps", "پښتو"),

    // Rest, sorted by their language code
    AR("ar", "العربية"),
    AZB("azb", "تۆرکجه"),
    CKB("ckb", "کوردی"),
    EN_IR("en", "English (Iran)"),
    EN_US("en-US", "English"),
    ES("es", "Español"),
    FR("fr", "Français"),
    GLK("glk", "گيلکي"),
    JA("ja", "日本語"),
    KMR("kmr", "Kurdî"),
    NE("ne", "नेपाली"),
    TG("tg", "Тоҷикӣ"),
    TR("tr", "Türkçe"),
    UR("ur", "اردو"),
    ZH_CN("zh-CN", "中文");

    val isArabic get() = this == AR
    val isDari get() = this == FA_AF
    val isPersian get() = this == FA
    val isIranianEnglish get() = this == EN_IR
    val isNepali get() = this == NE

    val showNepaliCalendar get() = this == NE

    val language get() = code.replace(Regex("-(IR|AF|US|CN)"), "")

    // en-IR and fa-AF aren't recognized by system, that's handled by #language
    fun asSystemLocale() = Locale(language)

    val inParentheses: String
        get() = when (this) {
            JA, ZH_CN -> "%s（%s）"
            else -> "%s (%s)"
        }

    // Formatting "Day Month Year" considerations
    val dmy: String
        get() = when (this) {
            CKB -> "%1\$sی %2\$sی %3\$s"
            ZH_CN -> "%3\$s 年 %2\$s %1\$s 日"
            else -> "%1\$s %2\$s %3\$s"
        }
    val dm: String
        get() = when (this) {
            CKB -> "%1\$sی %2\$s"
            JA, ZH_CN -> "%2\$s %1\$s"
            else -> "%1\$s %2\$s"
        }
    val my: String
        get() = when (this) {
            CKB -> "%1\$sی %2\$s"
            ZH_CN -> "%2\$s 年 %1\$s"
            else -> "%1\$s %2\$s"
        }
    val timeAndDateFormat: String
        get() = when (this) {
            JA, ZH_CN -> "%2\$s %1\$s"
            else -> "%1\$s$spacedComma%2\$s"
        }
    val clockAmPmOrder: String
        get() = when (this) {
            ZH_CN -> "%2\$s %1\$s"
            else -> "%1\$s %2\$s"
        }

    val isLessKnownRtl: Boolean
        get() = when (this) {
            AZB, GLK -> true
            else -> false
        }

    val betterToUseShortCalendarName: Boolean
        get() = when (this) {
            EN_US, JA, ZH_CN, FR, ES, AR, TR, TG -> true
            else -> false
        }

    val mightPreferUmmAlquraIslamicCalendar: Boolean
        get() = when (this) {
            FA_AF, PS, UR, AR, CKB, EN_US, JA, ZH_CN, FR, ES, TR, KMR, TG, NE -> true
            else -> false
        }


    val isHanafiMajority: Boolean
        get() = when (this) {
            TR, FA_AF, PS, TG, NE -> true
            else -> false
        }

    // Based on locale, we can presume user is able to read Persian
    val isUserAbleToReadPersian: Boolean
        get() = when (this) {
            FA, GLK, AZB, FA_AF, EN_IR -> true
            else -> false
        }

    val showIranTimeOption
        get() = when (this) {
            FA, AZB, CKB, EN_IR, EN_US, ES, FR, GLK, JA, ZH_CN, KMR -> true
            else -> false
        }

    // Whether locale uses الفبا or not
    val isArabicScript: Boolean
        get() = when (this) {
            EN_US, JA, ZH_CN, FR, ES, TR, KMR, EN_IR, TG, NE -> false
            else -> true
        }

    // Whether locale would prefer local digits like ۱۲۳ over the global ones, 123, initially at least
    val prefersLocalDigits: Boolean
        get() = when (this) {
            UR, EN_IR, EN_US, JA, ZH_CN, FR, ES, TR, KMR, TG -> false
            else -> true
        }

    // Whether the language doesn't need " and " between date parts or not
    val languagePrefersHalfSpaceAndInDates: Boolean
        get() = when (this) {
            JA, ZH_CN -> true
            else -> false
        }

    // Local digits (۱۲۳) make sense for the locale
    val canHaveLocalDigits get() = isArabicScript || isIranianEnglish || isNepali

    // Prefers ٤٥٦ over ۴۵۶
    val preferredDigits
        get() = when (this) {
            AR, CKB -> ARABIC_INDIC_DIGITS
            NE -> DEVANAGARI_DIGITS
            else -> PERSIAN_DIGITS
        }

    // We can presume user is from Afghanistan
    val isAfghanistanExclusive: Boolean
        get() = when (this) {
            FA_AF, PS -> true
            else -> false
        }

    // We can presume user is from Iran
    val isIranExclusive: Boolean
        get() = when (this) {
            AZB, GLK, FA, EN_IR -> true
            else -> false
        }

    // We can presume user would prefer Gregorian calendar at least initially
    private val prefersGregorianCalendar: Boolean
        get() = when (this) {
            EN_US, JA, ZH_CN, FR, ES, UR, TR, KMR, TG -> true
            else -> false
        }

    private val prefersNepaliCalendar: Boolean
        get() = isNepali

    // We can presume user would prefer Gregorian calendar at least initially
    private val prefersIslamicCalendar: Boolean
        get() = when (this) {
            AR -> true
            else -> false
        }

    // We can presume user would prefer Gregorian calendar at least initially
    private val prefersPersianCalendar: Boolean
        get() = when (this) {
            AZB, GLK, FA, FA_AF, PS, EN_IR -> true
            else -> false
        }


    val defaultWeekStart
        get() = when (this) {
            FA -> "0"
            TR, TG -> "2" // Monday
            else -> if (prefersGregorianCalendar || isNepali) "1"/*Sunday*/ else "0"
        }

    val defaultWeekEnds
        get() = when {
            this == FA -> setOf("6")
            isNepali -> setOf("0")
            prefersGregorianCalendar -> setOf("0", "1") // Saturday and Sunday
            else -> setOf("6") // 6 means Friday
        }

    val countriesOrder
        get() = when {
            isAfghanistanExclusive -> afCodeOrder
            isArabic -> arCodeOrder
            this == TR || this == KMR -> trCodeOrder
            else -> irCodeOrder
        }

    // Some languages don't have alphabet order matching with unicode order, this fixes them
    fun prepareForSort(text: String) = when {
        isArabicScript && !isArabic -> prepareForArabicSort(text)
        // We will need some preparation for non-English latin script
        // languages (Turkish, Spanish, French, ...) but our cities.json
        // don't have those a translation to those, so
        else -> text
    }

    private fun prepareForArabicSort(text: String) = text
        .replace("ی", "ي")
        .replace("ک", "ك")
        .replace("گ", "كی")
        .replace("ژ", "زی")
        .replace("چ", "جی")
        .replace("پ", "بی")
        .replace("و", "نی")
        .replace("ڕ", "ری")
        .replace("ڵ", "لی")
        .replace("ڤ", "فی")
        .replace("ۆ", "وی")
        .replace("ێ", "یی")
        .replace("ھ", "نی")
        .replace("ە", "هی")

    // Too hard to translate and don't want to disappoint translators thus
    // not moved yet to our common i18n system
    fun tryTranslateEclipseType(isSolar: Boolean, type: EclipseKind) = when (this) {
        EN_US, EN_IR -> {
            when {
                isSolar && type == EclipseKind.Annular -> "Annular solar eclipse"
                isSolar && type == EclipseKind.Partial -> "Partial solar eclipse"
                !isSolar && type == EclipseKind.Partial -> "Partial lunar eclipse"
                !isSolar && type == EclipseKind.Penumbral -> "Penumbral lunar eclipse"
                isSolar && type == EclipseKind.Total -> "Total solar eclipse"
                !isSolar && type == EclipseKind.Total -> "Total lunar eclipse"
                else -> null
            }
        }
        FA, FA_AF -> {
            when {
                isSolar && type == EclipseKind.Annular -> "خورشیدگرفتگی حلقه‌ای"
                isSolar && type == EclipseKind.Partial -> "خورشیدگرفتگی جزئی"
                !isSolar && type == EclipseKind.Partial -> "ماه‌گرفتگی جزئی"
                !isSolar && type == EclipseKind.Penumbral -> "ماه‌گرفتگی نیم‌سایه‌ای"
                isSolar && type == EclipseKind.Total -> "خورشیدگرفتگی کلی"
                !isSolar && type == EclipseKind.Total -> "ماه‌گرفتگی کلی"
                else -> null
            }
        }
        else -> null
    }

    companion object {
        @SuppressLint("ConstantLocale")
        val userDeviceLanguage = Locale.getDefault().language ?: "en"

        @SuppressLint("ConstantLocale")
        private val userDeviceCountry = Locale.getDefault().country ?: "IR"

        // Preferred app language for certain locale
        val preferredDefaultLanguage
            get() = when (userDeviceLanguage) {
                FA.code, "en", EN_US.code -> if (userDeviceCountry == "AF") FA_AF else FA
                else -> valueOfLanguageCode(userDeviceLanguage) ?: EN_US
            }

        fun valueOfLanguageCode(languageCode: String): Language? =
            values().find { it.code == languageCode }



        private val irCodeOrder = listOf("zz", "ir", "tr", "af", "iq")
        private val afCodeOrder = listOf("zz", "af", "ir", "tr", "iq")
        private val arCodeOrder = listOf("zz", "iq", "tr", "ir", "af")
        private val trCodeOrder = listOf("zz", "tr", "ir", "iq", "af")

        // See the naming here, https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type
        val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val ARABIC_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val ARABIC_INDIC_DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val DEVANAGARI_DIGITS = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
        // CJK digits: charArrayOf('０', '１', '２', '３', '４', '５', '６', '７', '８', '９')
        // but they weren't looking nice in the UI
    }
}
