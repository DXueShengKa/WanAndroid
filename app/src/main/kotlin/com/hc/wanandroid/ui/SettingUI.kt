package com.hc.wanandroid.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.common.heightCenterVertically
import com.hc.wanandroid.navigation.LocalNavController
import com.hc.wanandroid.navigation.Routes

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingUI() {
    val navController = LocalNavController.current
    val context = LocalContext.current

/*    var size by remember {
        mutableStateOf(IntSize(0, 0))
    }

    var d by remember {
        mutableStateOf(false)
    }

    if (d)
        Dialog({
            d = false
        }) {
            Box(
                Modifier
                    .size(300.dp)
                    .background(Color.White.copy(0.5f))
                    .onSizeChanged {
                        size = it
                    }
            ) {
                Text(text = "2222222", style = MaterialTheme.typography.h1)
            }
        }

    val oe = OffsetEffect(BlurEffect(
        10f,
        10f,
        TileMode.Repeated
    ), Offset(100f,100f))*/
    Column {
        Text(
            "本机缓存",
            Modifier
                .fillMaxWidth()
                .heightCenterVertically(30.dp)
                .clickable {
                    navController.navigate(Routes.LocalCache.name)
                },
            textAlign = TextAlign.Center
        )

        Text(
            "打开应用详情",
            Modifier
                .fillMaxWidth()
                .heightCenterVertically(30.dp)
                .clickable {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setData(Uri.fromParts("package", context.packageName, null))
                    )
                },
            textAlign = TextAlign.Center
        )

        /*  Image(
              Icons.Default.Person, null,
              Modifier
                  .fillMaxWidth()
                  .clickable {
                      d = true
                  }
          )

          Text(remember {
              String(CharArray(1000) { '1' })
          })*/
    }
}

private val BlurShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(Offset(100f, 100f), Size(400f, 400f)))
    }

}

@ExperimentalFoundationApi
@Preview(
    showBackground = true,
)
@Composable
fun Te() {
    LazyVerticalGrid(
        GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(60) {
            val s = if (it % 2 == 0) 80 else 60
            Card(Modifier.fillParentMaxWidth().height(s.dp)) {
                Text(it.toString())
            }
        }
    }
}