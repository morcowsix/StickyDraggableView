package com.example.dragableview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize

data class CardBoxState(
    var position: Offset,
    var size: IntSize,
    private val chipVisibility: MutableState<Boolean> = mutableStateOf(false),
    private var stickiedDraggableView: DraggableViewState? = null
) {
    fun contains(offset: Offset): Boolean {
        val rect = Rect(position, size.toSize())
        return rect.contains(offset)
    }

    fun chipIsVisible(): Boolean {
        return chipVisibility.value
    }

    fun releaseStickiedView() {
        chipVisibility.value = false
        stickiedDraggableView?.visibility?.value = true
        stickiedDraggableView = null
    }

    fun stickView(view: DraggableViewState) {
        chipVisibility.value = true
        stickiedDraggableView?.visibility?.value = true
        stickiedDraggableView = view
    }
}

fun CardBoxState.toRect(): Rect {
    return Rect(position, size.toSize())
}
