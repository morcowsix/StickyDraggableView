package com.example.dragableview

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val greenBoxStates = mutableSetOf<CardBoxState>()
    private val draggableViewStates = mutableSetOf<DraggableViewState>()

    fun checkCardsForIntersectionWithView(dragView: DraggableViewState): Boolean {
        Log.d(TAG, "onDragEnd: DragView: $dragView \n Current offset: ${dragView.currentOffset}")

        greenBoxStates.forEach { greenBox ->
            val contains = greenBox.contains(dragView.currentOffset)
            if (contains) {
                dragView.hide()
                greenBox.stickView(dragView)
                return true
            }
        }

        return false
    }

    fun addGreenBoxState(state: CardBoxState) {
        greenBoxStates.add(state)
    }

    fun addDraggableViewState(state: DraggableViewState) {
        draggableViewStates.add(state)
    }

    fun onCardClick(greenBox: CardBoxState) {
        greenBox.releaseStickiedView()
    }
}

