package com.google.accompanist.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * 把item分成两半
 * @param spacer 条目之间的间隔
 */
fun LazyListScope.fixed2(count: Int, spacer: Dp? = null, key : ((Int)->Any)? = null,content: @Composable (Int) -> Unit) {
	val m = count % 2
	val itemsCount = count / 2
	items(if (m == 0) itemsCount else itemsCount + 1, key) {
		Row(Modifier.fillParentMaxWidth()) {
			val x = it + it
			val modifier = Modifier.weight(1f)
			Box(
				if (spacer == null) modifier else modifier.padding(top = spacer, start = spacer, end = spacer / 2)
			) {
				content(x)
			}

			val x2 = x + 1
			if (x2 < count) {
				Box(
					if (spacer == null) modifier else modifier.padding(
						top = spacer,
						start = spacer / 2,
						end = spacer
					)
				) {
					content(x2)
				}
			} else {
				Spacer(modifier)
			}
		}
	}
}
