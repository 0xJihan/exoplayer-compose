package com.jihan.app.screens.components

import android.os.Build
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jihan.app.R
import com.jihan.app.model.VideoFile
import com.jihan.app.utils.toDate
import com.jihan.app.utils.toDurationString

@Composable
fun VideoItem(videoFile: VideoFile, onclick: () -> Unit) {

    val thumbnail =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) LocalContext.current.contentResolver.loadThumbnail(
            videoFile.pathUri,
            Size(640, 480),
            null
        )
        else null

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onclick() }
            .height(120.dp)
            .background(MaterialTheme.colorScheme.surface)) {

        Box(
            Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
                .clip(RoundedCornerShape(10))
        ) {


            AsyncImage(
                model = thumbnail ?: R.drawable.image_placeholder, null,
                Modifier
                    .width(180.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            Text(
                videoFile.duration.toDurationString(),
                Modifier
                    .padding(8.dp)
                    .background(Color.Gray)
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = 4.dp), color = MaterialTheme.colorScheme.onSurface
            )
        }


        Spacer(Modifier.width(15.dp))

        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {

            Text(
                videoFile.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                videoFile.pathUri.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 17.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                videoFile.dateAdded.toDate(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

    }

}
