package com.jihan.app.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Video.Media
import com.jihan.app.model.VideoFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VideoFilesRepo(private val context: Context) {

    fun getVideoFiles(): Flow<List<VideoFile>> = flow {
        val contextResolver = context.contentResolver ?: return@flow

        val projection = arrayOf(
            Media.DISPLAY_NAME,
            Media.DURATION,
            Media.SIZE,
            Media._ID,
            Media.DATE_ADDED
        )

        val queryCursor = contextResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${Media.DATE_ADDED} DESC"
        ) ?: return@flow

        queryCursor.use { cursor ->
            val videoFiles = mutableListOf<VideoFile>()
            while (cursor.moveToNext()) {
                videoFiles.add(cursor.toVideoFile())
            }
            emit(videoFiles)
        }
    }



}
    private fun Cursor.toVideoFile(): VideoFile {
        val name = getString(getColumnIndexOrThrow(Media.DISPLAY_NAME))
        val duration = getLong(getColumnIndexOrThrow(Media.DURATION))
        val size = getLong(getColumnIndexOrThrow(Media.SIZE))
        val id = getLong(getColumnIndexOrThrow(Media._ID))
        val dateAdded = getLong(getColumnIndexOrThrow(Media.DATE_ADDED))
        val videoUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id)

        return VideoFile(
            name = name,
            duration = duration,
            pathUri = videoUri,
            size = size,
            dateAdded = dateAdded
        )
    }
