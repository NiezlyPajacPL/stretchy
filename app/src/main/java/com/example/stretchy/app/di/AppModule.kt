package com.example.stretchy.app.di

import android.app.Application
import androidx.room.Room
import com.example.stretchy.app.di.scope.ApplicationScope
import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.AppDatabase.Companion.MIGRATION_1_3
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.RepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @ApplicationScope
    @Provides
    fun provideRepository(appDatabase: AppDatabase): Repository {
        return RepositoryImpl(appDatabase)
    }

    @ApplicationScope
    @Provides
    fun provideDataBase(application: Application): AppDatabase = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, AppDatabase.NAME
    ).addMigrations(MIGRATION_1_3).build()
}