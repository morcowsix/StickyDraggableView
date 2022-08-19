package com.example.stickydraggableview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize

/**
 * This class keep and handle StickyHolder values.
 */

data class StickyHolderState(
    var position: Offset,
    var size: IntSize,
    private val stickerVisibility: MutableState<Boolean> = mutableStateOf(false),
    private var stickiedDraggableView: DraggableViewState? = null,
) {

    /**
     * A function compare [StickyHolder] position with incoming offset and return boolean
     * value depending from offsets intersects or not.
     */
    fun contains(offset: Offset): Boolean {
        val rect = Rect(position, size.toSize())
        return rect.contains(offset)
    }

    fun stickerIsVisible(): Boolean {
        return stickerVisibility.value
    }

    /**
     * Detach a stickied view. Hide [Sticker], show [DraggableStickyView] if it exist
     * and then nullify field.
     */
    fun releaseStickiedView() {
        stickerVisibility.value = false
        stickiedDraggableView?.visibility?.value = true
        stickiedDraggableView = null
    }

    fun stickView(view: DraggableViewState) {
        stickerVisibility.value = true
        stickiedDraggableView?.visibility?.value = true
        stickiedDraggableView = view
    }

    fun getStickiedContent(): String {
        return stickiedDraggableView?.content.toString()
    }
}
