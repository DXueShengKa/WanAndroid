package com.hc.wanandroid.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.common.BrowsePicture
import com.google.accompanist.common.BrowsePictureState
import com.google.accompanist.insets.navigationBarsHeight

@Composable
fun MyBrowsePicture() {
//    val lo = LocalLifecycleOwner.current
    val c = remember {
        /*lo.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.e("LocalLifecycleOwner","$source $event")
            }
        })*/
        BrowsePictureState<Uri>()
    }

    val l = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        c.addAll(it)
    }

    Column {

        BrowsePicture(
            Modifier
                .fillMaxSize()
                .weight(1f),
            c
        ) { contentModifier,page ->
            Image(
                rememberImagePainter(data = page), null,
                contentModifier.fillMaxSize().border(1.dp, Color.Green)
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("添加", Modifier.clickable {
                l.launch("image/*")
            })

        }

        Spacer(Modifier.navigationBarsHeight())
    }
}
