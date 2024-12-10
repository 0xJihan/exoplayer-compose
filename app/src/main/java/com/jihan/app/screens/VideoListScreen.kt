package com.jihan.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jihan.app.model.VideoFile
import com.jihan.app.screens.components.VideoItem

@Composable
fun VideoListScreen(videoList: List<VideoFile>,onClick:(String)->Unit) {
    LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        if (videoList.isEmpty().not())
            items(videoList){
                VideoItem(it){
                    onClick(it.pathUri.toString())
                }
            }
        else
            item {
                Text(text = "No Video Found")
            }
    }
}