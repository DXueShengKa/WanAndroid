package com.hc.wanandroid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.text.FontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.hc.wanandroid.MainActivity

/**
 * Simple widget for showcasing how a widget using Glace is constructed.
 */
class FirstGlanceWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .padding(8.dp)
        ) {
            Text(
                text = "微件",
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(fontWeight = FontWeight.Bold),
            )
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    text = "button1",
                    onClick = actionStartActivity<MainActivity>()
                )
                Button(
                    text = "button2",
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }
}

class FirstGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = FirstGlanceWidget()
}

@Composable
fun WidgetC(){

}