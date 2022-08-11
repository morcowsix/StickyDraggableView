package com.example.dragableview

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntRect

class MainViewModel: ViewModel() {
    val dragViewState by mutableStateOf(DragViewState(Offset.Zero, Offset.Zero, mutableStateOf(true)))
//    val greenBoxState by mutableStateOf(GreenBoxState(Offset.Zero, IntSize.Zero, mutableStateOf(false)))

    val listOFGreenBoxStates = mutableSetOf<GreenBoxState>()

    var testState by mutableStateOf(TestSate(0f, 0f))

    var rowLayoutSize = IntRect(IntOffset.Zero, IntSize.Zero)


    fun onDragEnd(currentPosition: Offset): Boolean {

        dragViewState.currentOffset = currentPosition
        Log.d(TAG, "onDragEnd: Current offset: ${dragViewState.currentOffset}")

        listOFGreenBoxStates.forEach {
            val contains = it.contains(dragViewState.currentOffset)
            if (contains) {
                it.visibility.value = true
                dragViewState.visibility.value = false
                dragViewState.currentOffset = dragViewState.startOffset
                return true
            }
        }

        return false
    }

    fun onOffsetChange(offsetX: Float, offsetY: Float) {
//        dragViewState.value.currentOffset = Offset(offsetX, offsetY)
        Log.d(ContentValues.TAG, "onOffsetChange: ${dragViewState}")

        testState = TestSate(offsetX, offsetY)
    }

    fun addState(state: GreenBoxState) {
//        listOFGreenBoxStates.clear()
        listOFGreenBoxStates.add(state)
        Log.d(TAG, "addState: $state added")
    }

    fun setPosition(rect: Rect) {

    }

    fun onCardClick(greenBoxState: GreenBoxState) {
        greenBoxState.visibility.value = false
        dragViewState.visibility.value = true
    }
}

