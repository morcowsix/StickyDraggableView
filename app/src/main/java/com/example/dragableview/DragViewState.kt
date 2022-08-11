package com.example.dragableview

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

data class DragViewState(
    var startOffset: Offset,
    var currentOffset: Offset,
    var visibility: MutableState<Boolean>
)

fun DragViewState.currentOffsetToIntOffset(): IntOffset {
    return IntOffset(currentOffset.x.roundToInt(), currentOffset.x.roundToInt())
}
