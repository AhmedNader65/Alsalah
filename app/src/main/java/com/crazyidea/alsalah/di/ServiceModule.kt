package com.crazyidea.alsalah.di

import android.content.Context
import com.crazyidea.alsalah.BuildConfig
import com.crazyidea.alsalah.data.api.CalendarAPI
import com.crazyidea.alsalah.data.api.PrayersAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {


    @Provides
    @Named("prayers_url")
    fun provideBaseUrl() = BuildConfig.PRAYERS_BASE_URL


    @Provides
    @Named("wiki_url")
    fun provideWikiUrl() = BuildConfig.WIKI_BASE_URL



    @Provides
    @Singleton
    fun providePrayersApi(retrofit: Retrofit): PrayersAPI = retrofit.create(PrayersAPI::class.java)

    @Provides
    @Singleton
    fun provideCalendarApi(retrofit: Retrofit): CalendarAPI = retrofit.create(CalendarAPI::class.java)

    @Provides
    @Singleton
    @Inject
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ) = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
//            .addInterceptor(authInterceptor)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    } else {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(context.cacheDir, cacheSize.toLong())
        OkHttpClient
            .Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient,@Named("prayers_url") BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

}