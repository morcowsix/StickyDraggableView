package com.example.dragableview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.zIndex

/**
 * A layout composable with [content].
 * Using for fill [StickyHolder] as secondary component. The first one can be any Composable.
 * The [Sticker] is usually filled with a static copy of the [DraggableStickyView] but can be
 * filled with any Composable. To remove a [Sticker] from [StickyHolder], the nested component
 * must have releaseStickiedView function from [StickyHolderState].
 *
 * @param modifier The modifier to be applied to the layout.
 * @param visible The visible state of Sticker.
 * @param content The content of the [Sticker].
 */

@Composable
fun Sticker(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier
            .zIndex(2f),
        visible = visible
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
