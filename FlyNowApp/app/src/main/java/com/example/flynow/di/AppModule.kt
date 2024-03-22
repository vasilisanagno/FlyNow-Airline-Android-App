package com.example.flynow.di

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.data.network.FlyNowApiImpl
import com.example.flynow.data.repository.CheckRepository
import com.example.flynow.data.repository.FinishReservationRepository
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//classes that are visible throughout the app
@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideRequestQueue(@ApplicationContext context: Context): RequestQueue {
        return Volley.newRequestQueue(context.applicationContext)
    }

    @Provides
    @Singleton
    fun provideFlyNowApi(requestQueue: RequestQueue): FlyNowApi {
        return FlyNowApiImpl(requestQueue)
    }

    @Provides
    @Singleton
    fun provideCheckRepository(flyNowApi: FlyNowApi): CheckRepository {
        return CheckRepository(flyNowApi)
    }

    @Provides
    @Singleton
    fun provideFinishReservationRepository(flyNowApi: FlyNowApi): FinishReservationRepository {
        return FinishReservationRepository(flyNowApi)
    }

    @Provides
    @Singleton
    fun provideSharedViewModel(checkRepository: CheckRepository): SharedViewModel {
        return SharedViewModel(checkRepository)
    }

    @Provides
    @Singleton
    fun provideBaggageAndPetsViewModel(
        repository: FinishReservationRepository,
        sharedViewModel: SharedViewModel
    ): BaggageAndPetsViewModel {
        return BaggageAndPetsViewModel(repository, sharedViewModel)
    }
}