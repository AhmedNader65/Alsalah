package com.crazyidea.alsalah.di

import android.content.Context
import com.crazyidea.alsalah.BuildConfig
import com.crazyidea.alsalah.data.api.ArticlesAPI
import com.crazyidea.alsalah.data.api.AuthAPI
import com.crazyidea.alsalah.data.api.CalendarAPI
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
    @Named("base_url")
    fun provideAppBaseUrl() = BuildConfig.BASE_URL


    @Provides
    @Named("wiki_url")
    fun provideWikiUrl() = BuildConfig.WIKI_BASE_URL


}