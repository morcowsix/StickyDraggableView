package com.example.dragableview

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val stickyHoldersStates = mutableSetOf<StickyHolderState>()
    private val draggableViewStates = mutableSetOf<DraggableViewState>()

    fun checkCardsForIntersection(draggableView: DraggableViewState): Boolean {
        stickyHoldersStates.forEach { stickyHolder ->
            val contains = stickyHolder.contains(draggableView.currentOffset)
            if (contains) {
                draggableView.hide()
                stickyHolder.releaseStickiedView()
                stickyHolder.stickView(draggableView)
                return true
            }
        }

        return false
    }

    fun addStickyHolderState(state: StickyHolderState) {
        stickyHoldersStates.add(state)
    }

    fun addDraggableViewState(state: DraggableViewState) {
        draggableViewStates.add(state)
    }

    fun onStickerClick(stickyHolder: StickyHolderState) {
        stickyHolder.releaseStickiedView()
    }
}

