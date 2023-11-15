package com.learn.worlds.data.remote

import com.learn.worlds.BuildConfig
import com.learn.worlds.data.remote.ai.ktorHttpClient
import com.learn.worlds.utils.Keys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.EIDEN_BASE_API)
        .client(okHttpClient)
        .build()


    @Provides
    @Singleton
    fun provideCardApi(retrofit: Retrofit) = retrofit.create(CardsApi::class.java)

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return ktorHttpClient
    }
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
        }
        okHttpBuilder.addInterceptor(Interceptor {
            chain ->
            chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + Keys.token).build())
        })
        return okHttpBuilder.build()
    }

}