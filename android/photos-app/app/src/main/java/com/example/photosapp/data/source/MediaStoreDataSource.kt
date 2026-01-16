package com.example.photosapp.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import com.example.photosapp.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Data source that fetches photos and videos from the device's MediaStore.
 */
class MediaStoreDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    /**
     * Queries MediaStore for all photos and videos on the device.
     * Returns items sorted by date taken (newest first).
     */
    suspend fun getPhotos(): List<Photo> = withContext(Dispatchers.IO) {
        val items = mutableListOf<Photo>()

        // Query images
        items.addAll(queryImages())

        // Query videos
        items.addAll(queryVideos())

        // Sort combined list by date taken (newest first)
        items.sortedByDescending { it.dateTaken }
    }

    private fun queryImages(): List<Photo> {
        val photos = mutableListOf<Photo>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = mutableListOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )

        // IS_FAVORITE is only available on API 30+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projection.add(MediaStore.Images.Media.IS_FAVORITE)
        }

        contentResolver.query(
            collection,
            projection.toTypedArray(),
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val favoriteColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getColumnIndex(MediaStore.Images.Media.IS_FAVORITE)
            } else -1

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val isFavorite = if (favoriteColumn >= 0) cursor.getInt(favoriteColumn) == 1 else false

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                photos.add(
                    Photo(
                        id = id,
                        uri = contentUri,
                        dateTaken = dateTaken,
                        width = width,
                        height = height,
                        isVideo = false,
                        duration = 0L,
                        isFavorite = isFavorite
                    )
                )
            }
        }

        return photos
    }

    private fun queryVideos(): List<Photo> {
        val videos = mutableListOf<Photo>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = mutableListOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DURATION
        )

        // IS_FAVORITE is only available on API 30+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projection.add(MediaStore.Video.Media.IS_FAVORITE)
        }

        contentResolver.query(
            collection,
            projection.toTypedArray(),
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val favoriteColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getColumnIndex(MediaStore.Video.Media.IS_FAVORITE)
            } else -1

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val duration = cursor.getLong(durationColumn)
                val isFavorite = if (favoriteColumn >= 0) cursor.getInt(favoriteColumn) == 1 else false

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                videos.add(
                    Photo(
                        id = id,
                        uri = contentUri,
                        dateTaken = dateTaken,
                        width = width,
                        height = height,
                        isVideo = true,
                        duration = duration,
                        isFavorite = isFavorite
                    )
                )
            }
        }

        return videos
    }
}
