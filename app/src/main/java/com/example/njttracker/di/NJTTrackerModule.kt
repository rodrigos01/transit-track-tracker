package com.example.njttracker.di

import com.example.njttracker.common.analytics.AnalyticsLogger
import com.example.njttracker.common.analytics.firebase.FirebaseAnalyticsLogger
import com.example.njttracker.common.data.RemoteDataStore
import com.example.njttracker.common.network.Api
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface NJTTrackerModule {
    @Binds
    fun bindRemoteDataStore(
        impl: Api
    ): RemoteDataStore

    @Binds
    fun bindAnalyticsLogger(impl: FirebaseAnalyticsLogger): AnalyticsLogger
}