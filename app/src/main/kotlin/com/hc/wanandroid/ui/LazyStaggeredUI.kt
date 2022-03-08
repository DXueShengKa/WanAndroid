package com.hc.wanandroid.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.common.LazyStaggeredGrid
import com.google.accompanist.common.StaggeredCells

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyStaggeredUI(){
    LazyStaggeredGrid(cells = StaggeredCells.Adaptive(minSize = 180.dp)) {
        items(60) {
            val random: Double = 100 + Math.random() * (500 - 100)
            Box(
                modifier = Modifier
                    .border(1.dp, Color.Green)
                    .fillParentMaxWidth()
                    .height(random.dp)
                    .padding(10.dp),
            )
        }
    }

}