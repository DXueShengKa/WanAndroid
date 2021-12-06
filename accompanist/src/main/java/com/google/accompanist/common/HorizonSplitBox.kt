package com.google.accompanist.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun HorizonSplitBox(
	modifier: Modifier,
	oneHeight: Dp,
	showBorder: Boolean = true,
	contents: SplitBox.() -> Unit
) {
	val splitBox = remember { SplitBox(mutableListOf()) }
	splitBox.clear()
	contents(splitBox)

	val color = Color.LightGray.copy(alpha = 0.5f)
	HorizonEquidistant(
		fixed = splitBox.size,
		modifier = modifier,
		weights = FloatArray(splitBox.size){ splitBox[it].second },
		oneHeight = oneHeight,
		color = if(showBorder) color else Color.Unspecified,
		verticalArrangement = Arrangement.Center
	) {
		splitBox.forEach {
			it.first()
		}
	}
	if (showBorder)
		Divider(color = color)
}

private typealias SplitBoxContent = MutableList<Pair<@Composable () -> Unit, Float>>

class SplitBox(content:SplitBoxContent) : SplitBoxContent by content {

	fun add(weight: Float = 1f, content: @Composable () -> Unit) {
		add(content to weight)
	}

}