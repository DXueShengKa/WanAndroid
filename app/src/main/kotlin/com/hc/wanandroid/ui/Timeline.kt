package com.hc.wanandroid.ui


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.common.HorizonEquidistant
import com.google.accompanist.common.HorizonSplitBox
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode


//@Preview(showBackground = true)
@Composable
fun TimelineUI() {

    TwoTexts("111","222")

/*    Column(Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                Box(Modifier.fillParentMaxWidth().padding(vertical = 10.dp)) {
                    Text(stringResource(R.string.album_management))
                    Icon(
                        painterResource(R.mipmap.ic_timeline_calendar),
                        null,
                        Modifier.size(18.dp).align(
                            Alignment.CenterEnd
                        ),
                        Color.Green
                    )
                }
            }
            items(10) {
                Row(Modifier.fillParentMaxWidth().height(IntrinsicSize.Min)) {
                    TimelineItem()
                }
            }
        }
    }*/
}


@Composable
fun TwoTexts(
    text1: String,
    text2: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        /*  Text(
              modifier = Modifier
                  .weight(1f)
                  .padding(start = 4.dp)
                  .wrapContentWidth(Alignment.Start),
              text = text1
          )*/
        Box(
            modifier = Modifier.fillMaxHeight().width(1.dp).background(Color.Red)
        )
        Column(Modifier.weight(1f).border(1.dp, Color.Black)){

            Box(Modifier.fillMaxWidth()) {
                Text("2020-2-1")
                Image(
                    painterResource(com.hc.wanandroid.R.drawable.ybmq),
                    null,
                    Modifier.align(Alignment.CenterEnd),
                )
            }

            Text("ssss",Modifier.padding(vertical = 10.dp))

            HorizonEquidistant{
                Box(Modifier.size(70.dp).background(Color.Green))
            }
          
     /*       FlowRow(

                mainAxisSize = SizeMode.Wrap,
                mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                crossAxisSpacing = 5.dp
            ) {
                repeat(9){
                    Box(Modifier.size(80.dp).background(Color.Green))
                }
            }*/
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.TimelineItem() {

    Column(Modifier.width(20.dp).border(1.dp, Color.Red), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.MoreVert,
            null,
            Modifier.size(15.dp),
            Color.Green
        )
        Box(Modifier.fillMaxHeight().width(2.dp).background(Color.Green))
    }

    Column(Modifier.weight(1f).border(1.dp, Color.Black)) {
        Box(Modifier.fillMaxWidth()) {
            Text("2020-2-1")
            Image(
                Icons.Default.AccountBox,
                null,
                Modifier.align(Alignment.CenterEnd),
            )
        }

        Text("ssss",Modifier.padding(vertical = 10.dp))

        FlowRow(
            Modifier.fillMaxWidth(),
            mainAxisSize = SizeMode.Wrap,
            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
            crossAxisSpacing = 5.dp
        ) {
            repeat(9){
                Box(Modifier.size(80.dp).background(Color.Green))
            }
        }
    }
}