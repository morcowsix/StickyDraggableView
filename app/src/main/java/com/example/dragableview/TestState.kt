package com.example.dragableview

import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

data class TestSate(
    var x: Float,
    var y: Float
)

fun TestSate.toIntOffset(): IntOffset {
    return IntOffset(x.roundToInt(), y.roundToInt())
}