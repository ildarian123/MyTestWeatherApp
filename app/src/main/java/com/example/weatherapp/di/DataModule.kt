package com.example.weatherapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.weatherapp.data.network.GeocoderApi
import com.example.weatherapp.data.network.NetworkApi
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.squareup.picasso.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/"
        private const val GEOCODER_API_URL = "https://maps.googleapis.com/"
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        networkApi: NetworkApi,
        geocoderApi: GeocoderApi
    ): WeatherRepositoryImpl {
        return WeatherRepositoryImpl(networkApi, geocoderApi)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideGeocoderApi(client: OkHttpClient): GeocoderApi {
        return Retrofit.Builder()
            .baseUrl(GEOCODER_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GeocoderApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkApi(client: OkHttpClient): NetworkApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(NetworkApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
    }

}