package com.example.dragableview

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize

data class GreenBoxState(
    var position: Offset,
    var size: IntSize,
    var visibility: MutableState<Boolean>
) {
    fun contains(offset: Offset): Boolean {
        val rect = Rect(position, size.toSize())
        return rect.contains(offset)
    }
}

fun GreenBoxState.toRect(): Rect {
    return Rect(position, size.toSize())
}
