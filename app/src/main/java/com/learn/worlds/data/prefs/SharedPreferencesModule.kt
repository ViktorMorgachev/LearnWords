package com.learn.worlds.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.learn.worlds.di.MainPreferences
import com.learn.worlds.di.SynckPreferences
import com.learn.worlds.di.UIPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Provides
    @MainPreferences
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("learnWords_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @UIPreferences
    @Singleton
    fun provideUISharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("learnWords_preferences_ui", Context.MODE_PRIVATE)
    }

    @Provides
    @SynckPreferences
    @Singleton
    fun provideSynckSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("learnWords_preferences_sync", Context.MODE_PRIVATE)
    }



}