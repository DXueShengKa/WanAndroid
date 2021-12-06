package com.google.accompanist.common

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


@Stable
class ImeScroll(val scrollState: ScrollState, val fieldY:FieldY)


@Composable
fun rememberImeScrollState(): ImeScroll {
	val fieldY = remember { FieldY(0) }
	val state = rememberImeScrollState(fieldY)
	return ImeScroll(state,fieldY)
}

/**
 * 需要在 activity 中设置 android:windowSoftInputMode="adjustResize"
 */
@Composable
private fun rememberImeScrollState(fieldY:FieldY): ScrollState {
	val scrollState = rememberSaveable(saver = ScrollState.Saver) {
		ScrollState(initial = 0)
	}
	val v = LocalView.current
	val isImeVisible = remember{
		val visibleState = mutableStateOf(false)
		//监听键盘是否显示
		ViewCompat.setOnApplyWindowInsetsListener(v.rootView) { _, insets ->
			visibleState.value = insets.isVisible(WindowInsetsCompat.Type.ime())
			insets
		}
		visibleState
	}

	//键盘弹起来时滑动到焦点组件顶部
	if (isImeVisible.value) {
		LaunchedEffect(fieldY) {
			scrollState.animateScrollTo(fieldY.value - 20)
		}
	}
	return scrollState
}

/**
 * 文本框所在位置的y坐标值
 */
class FieldY(@JvmField var value:Int)

fun Modifier.imeScroll(fieldY:FieldY): Modifier {
	var positionInParent = 0f
	return this
		.onGloballyPositioned {
			//当前组件相对父组件的y坐标
			positionInParent = it.positionInParent().y
		}
		.onFocusChanged {
			//焦点
			if (it.isFocused) {
				fieldY.value = positionInParent.toInt()
			}
		}
}
