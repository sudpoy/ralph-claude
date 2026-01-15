package com.example.photosapp.data.di

import android.content.ContentResolver
import android.content.Context
import com.example.photosapp.data.repository.PhotoRepositoryImpl
import com.example.photosapp.domain.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository

    companion object {
        @Provides
        @Singleton
        fun provideContentResolver(
            @ApplicationContext context: Context
        ): ContentResolver {
            return context.contentResolver
        }
    }
}
