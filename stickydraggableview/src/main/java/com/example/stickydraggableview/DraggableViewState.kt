package com.example.stickydraggableview

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

data class DraggableViewState(
    var startOffset: Offset,
    var currentOffset: Offset,
    var visibility: MutableState<Boolean>,
    var content: Any? = null
) {
    fun hide() {
        visibility.value = false
    }
}

fun DraggableViewState.currentOffsetToIntOffset(): IntOffset {
    return IntOffset(currentOffset.x.roundToInt(), currentOffset.x.roundToInt())
}
