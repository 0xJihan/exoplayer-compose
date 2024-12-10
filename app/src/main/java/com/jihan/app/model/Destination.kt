package com.jihan.app.model



import kotlinx.serialization.Serializable

sealed interface Destination {

    @Serializable
    data object VideoList : Destination

    @Serializable
    data class VideoPlayer(
        val uri: String
    ) : Destination



}