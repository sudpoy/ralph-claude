package com.example.photosapp.domain.model

import android.net.Uri

/**
 * Domain model representing a photo or video on the device.
 */
data class Photo(
    val id: Long,
    val uri: Uri,
    val dateTaken: Long,
    val width: Int,
    val height: Int,
    val isVideo: Boolean = false,
    val duration: Long = 0L,
    val isFavorite: Boolean = false
)
