package com.dazzlerr_usa.utilities.dagger

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class DaggerModule(public val context: Context)
{

    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        val PREFERENCE_NAME = "DazzlerrPreferences"
    }

}
