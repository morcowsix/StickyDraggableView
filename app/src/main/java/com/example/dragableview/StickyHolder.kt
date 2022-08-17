package com.example.dragableview

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex


@Composable
fun StickyHolder(
    modifier: Modifier = Modifier,
    state: StickyHolderState,
    rememberState: (StickyHolderState) -> Unit,
    stickerAlignment: Alignment = Alignment.BottomCenter,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(Unit) {
        rememberState(state)
    }

    Layout(
        content = content,
        measurePolicy = measurePolicy(stickerAlignment),
        modifier = modifier
            .zIndex(1f)
            .onGloballyPositioned { coordinates ->
                state.size = coordinates.size
                state.position = coordinates.positionInRoot()
            }
    )
}

internal fun measurePolicy(stickerAlignment: Alignment) =
    MeasurePolicy { measurables, constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            measurable.measure(looseConstraints)
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeInStickyHolder(placeable, index, constraints, stickerAlignment)
            }
        }
    }

private fun Placeable.PlacementScope.placeInStickyHolder(
    placeable: Placeable,
    index: Int,
    constraints: Constraints,
    stickerAlignment: Alignment
) {
    var position = IntOffset(0,0) //set main content position

    if (index != 0) { //set sticker position
        when (stickerAlignment) {
            Alignment.TopStart -> {
                position = IntOffset(0,0)
            }
            Alignment.Center -> {
                position = IntOffset(
                    constraints.maxWidth/2 - placeable.width/2,
                    constraints.maxHeight/2 - placeable.height/2
                )
            }
            Alignment.BottomCenter -> {
                position = IntOffset(
                    constraints.maxWidth/2 - placeable.width/2,
                    constraints.maxHeight - placeable.height
                )
            }
            else -> position = IntOffset(0,0)
        }
    }

    placeable.place(position)
}