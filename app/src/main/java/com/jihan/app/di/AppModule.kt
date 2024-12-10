package com.jihan.app.di

import androidx.media3.exoplayer.ExoPlayer
import com.jihan.app.repository.VideoFilesRepo
import com.jihan.app.viewmodel.VideoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {

    single {
        VideoFilesRepo(androidContext())
    }

    viewModelOf(::VideoViewModel)

    single {
        ExoPlayer.Builder(androidContext()).build()
    }

}


