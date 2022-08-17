package com.example.dragableview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.zIndex

@Composable
fun Sticker(
    modifier: Modifier = Modifier,
    state: StickyHolderState,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier
            .zIndex(2f),
        visible = state.stickerIsVisible()
    ) {
        Layout(
            content = content
        ) { measurables, constraints ->
            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )

            val placeables = measurables.map { measurable ->
                measurable.measure(looseConstraints)
            }

            val layoutWidth = placeables.maxOfOrNull { it.width } ?: 0
            val layoutHeight = placeables.maxOfOrNull { it.height } ?: 0

            layout(layoutWidth, layoutHeight) {
                var xPosition = layoutWidth/2
                var yPosition = layoutHeight/2


                placeables.forEach { placeable ->
                    xPosition -= placeable.width/2
                    yPosition -= placeable.height/2

                    placeable.placeRelative(x = xPosition, y = yPosition)
                }
            }
        }
    }
}
