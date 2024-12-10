package com.jihan.app.model

import android.net.Uri
import kotlinx.serialization.Serializable


data class VideoFile(
    val name:String,
    val duration:Long,
    val pathUri: Uri,
    val size:Long,
    val dateAdded:Long,
    val thumbnailPath:String?=null
)
