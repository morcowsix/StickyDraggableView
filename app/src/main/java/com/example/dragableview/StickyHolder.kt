package com.example.dragableview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex

/**
 * A layout composable with [content].
 * [StickyHolder] using as a keeper of main content and [Sticker].The first component should be
 * any Composable that you want to use as the main content (background for [Sticker]).
 * The second component is a [Sticker] containing a Composable that will be attached to the main
 * content (background). Stickied [Sticker] can be removed from [StickyHolder] by
 * releaseStickiedView function in [StickyHolderState] class.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The state supplying information about [StickyHolder] position and stickied
 * [DraggableStickyView].
 * @param rememberState The function used for remember [StickyHolder]'s state.
 * @param stickerAlignment The default sticker alignment inside the [StickyHolder].
 * @param content The content of the [StickyHolder].
 */

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