package com.jihan.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.jihan.app.model.VideoFile
import com.jihan.app.repository.VideoFilesRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

class VideoViewModel(
    private val repo: VideoFilesRepo,
    private val player:ExoPlayer
) : ViewModel() {

    val exoPlayer get() = player

    val videoFiles = repo.getVideoFiles().catch {
        it.printStackTrace()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


}