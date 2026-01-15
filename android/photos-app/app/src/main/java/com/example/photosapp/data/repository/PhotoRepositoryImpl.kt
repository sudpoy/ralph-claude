package com.example.photosapp.data.repository

import com.example.photosapp.data.source.MediaStoreDataSource
import com.example.photosapp.domain.model.Photo
import com.example.photosapp.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of PhotoRepository that uses MediaStoreDataSource
 * to fetch photos from the device's storage.
 */
class PhotoRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource
) : PhotoRepository {

    override fun getPhotos(): Flow<List<Photo>> = flow {
        val photos = mediaStoreDataSource.getPhotos()
        emit(photos)
    }
}
