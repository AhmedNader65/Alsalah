package com.crazyidea.alsalah.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crazyidea.alsalah.BuildConfig
import com.crazyidea.alsalah.data.api.Network
import com.crazyidea.alsalah.data.model.Language
import com.crazyidea.alsalah.data.model.asAyatDatabaseModel
import com.crazyidea.alsalah.data.model.asDatabaseModel
import com.crazyidea.alsalah.data.model.asEditionDatabaseModel
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.*
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.utils.GlobalPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class QuranRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val globalPreferences: GlobalPreferences,
    private val externalScope: CoroutineScope
) {
    val bookmarks: LiveData<List<BookmarkWithAya>> = appDatabase.quranDao().getBookmarks()

    suspend fun getPage(page: Int = 1): List<Ayat> {

        return withContext(externalScope.coroutineContext) {
            return@withContext appDatabase.quranDao().getPage(page)
        }
    }

    suspend fun getAllSurah(): List<Surah> {

        return withContext(externalScope.coroutineContext) {
            return@withContext appDatabase.quranDao().getAllSurah()
        }
    }

    suspend fun downloadQuran(): Boolean {

        return withContext(externalScope.coroutineContext) {
            if (appDatabase.quranDao()
                    .getEditionByLanguage(globalPreferences.getLocale()) == null
            ) {
                val quran = Network.quran.getQuran()
                appDatabase.quranDao().emptyAyat()
                appDatabase.quranDao().emptySurah()
                appDatabase.quranDao().emptyEdition()
                appDatabase.quranDao()
                    .insertSurah(*quran.data.surahs.asDatabaseModel().toTypedArray())
                appDatabase.quranDao().insertEdition(quran.data.edition.asEditionDatabaseModel())
                quran.data.surahs.forEach {
                    appDatabase.quranDao().insertAyah(*it.asAyatDatabaseModel().toTypedArray())
                }
            }
            return@withContext true
        }
    }

    suspend fun getAudio(ayah: String, ayahId: Int, page: Int): String {
        return withContext(externalScope.coroutineContext) {
            val id = appDatabase.quranDao().getAyaId(ayah, ayahId, page)
            return@withContext Network.quran.getAudio(BuildConfig.QURAN_BASE_URL + "ayah/$id/ar.mahermuaiqly").data.audio
        }
    }

    suspend fun getJuzPage(toInt: Int): Int {
        return withContext(externalScope.coroutineContext) {
            return@withContext appDatabase.quranDao().getJuzPage(toInt)
        }
    }

    suspend fun bookmarkPage(page: Long) {
        return withContext(externalScope.coroutineContext) {
            val pageBookmarked = appDatabase.quranDao().isPageBookmarked(page).isNotEmpty()
            if (pageBookmarked) {
                appDatabase.quranDao().deletePageBookmark(page)
            } else {
                appDatabase.quranDao().bookmark(Bookmarks(null, page = page))
            }
        }
    }

    suspend fun bookmarkAya(text:String,ayaId: Int,page:Int) {
        return withContext(externalScope.coroutineContext) {
            val id = appDatabase.quranDao().getAyaId(text, ayaId, page).toLong()
            val ayaBookmarked = appDatabase.quranDao().isAyaBookmarked(id)
            appDatabase.quranDao().updateAya(AyatBookMark(id, !ayaBookmarked))
            if (ayaBookmarked) {
                appDatabase.quranDao().deleteAyaBookmark(id)
            } else {
                appDatabase.quranDao().bookmark(Bookmarks(null, aya = id))
            }
        }
    }

}