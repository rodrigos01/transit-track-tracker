package com.example.njttracker.di

import com.example.njttracker.common.data.LocalDataStore
import com.example.njttracker.common.persistence.preferences.PreferencesStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PersistenceModule {
    @Binds
    @Singleton
    fun bindLocalDataStore(
        impl: PreferencesStore
    ): LocalDataStore
}