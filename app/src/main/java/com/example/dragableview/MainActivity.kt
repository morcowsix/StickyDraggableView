package com.example.dragableview

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.example.dragableview.ui.theme.DragableViewTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel by viewModels()
            DragableViewTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier.zIndex(2f)) {
                            DragView(
                                state = viewModel.dragViewState,
                                onDragEnd = viewModel::onDragEnd,
                                onOffsetChange = viewModel::onOffsetChange,
                                testState = viewModel.testState
                            )
                        }
//                        AnimatableDragView()

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    viewModel.rowLayoutSize = coordinates.size.toIntRect()
//                                    Log.d(TAG, "onCreate: row layout size: ${coordinates.boundsInRoot()}")
                                }
                                .horizontalScroll(
                                    state = ScrollState(0)
                                )
                        ) {
                            (0..5).forEach {
                                GreenBox(GreenBoxState(
                                    Offset.Zero, IntSize.Zero, mutableStateOf(false)),
                                    viewModel::addState,
                                    onClick = viewModel::onCardClick
                                )
                            }
                        }
//                        GreenBox(viewModel.greenBoxState, viewModel::addState)
                    }
                }
            }
        }
    }
}

@Composable
fun DragView(
    state: DragViewState,
    onDragEnd: (Offset) -> Boolean,
    onOffsetChange: (Float, Float) -> Unit,
    testState: TestSate
) {
    val coroutineScope = rememberCoroutineScope()
    var position by remember { mutableStateOf(Offset.Zero) }
    val animatedOffset by remember {
        mutableStateOf(Animatable(state.startOffset, Offset.VectorConverter))
    }
    val animatedAlpha by remember { mutableStateOf(Animatable(1f)) }

    Log.d(TAG, "DragView: RECOMPOSE")

    if (state.visibility.value) {
        Box(
            Modifier
                .offset {
                    IntOffset(
                        animatedOffset.value.x.roundToInt(),
                        animatedOffset.value.y.roundToInt(),
                    )
                }
                .graphicsLayer(alpha = animatedAlpha.value)
                .zIndex(2f)
                .background(Blue)
                .size(50.dp)
                .onGloballyPositioned { coords ->
                    position = coords.positionInRoot()
//                        Log.d(TAG, "DragView: global position: $position")
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            state.startOffset = animatedOffset.value
                        },
                        onDragEnd = {
                            val isCardArea = onDragEnd(position)
                            if (isCardArea) {
                                coroutineScope.launch {
                                    animatedOffset.snapTo(state.startOffset)
                                }
                            } else {
                                coroutineScope.launch {
                                    animatedOffset.animateTo(
                                        targetValue = state.startOffset,
                                        animationSpec = tween(
                                            durationMillis = 1000,
                                            delayMillis = 0
                                        )
                                    )
                                }
                            }
                        }
                    ) { change, dragAmount ->
//                            change.consumeAllChanges()
                        coroutineScope.launch {
                            animatedOffset.snapTo(
                                Offset(
                                    animatedOffset.value.x + dragAmount.x,
                                    animatedOffset.value.y + dragAmount.y,
                                )
                            )
                        }
                    }
                }

        )
    }


}

@Composable
fun GreenBox(
    state: GreenBoxState,
    addState: (GreenBoxState) -> Unit,
    onClick: (GreenBoxState) -> Unit
) {
//    var size by remember { mutableStateOf(IntSize.Zero) }
//    var position by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(Unit) {
        addState(state)
    }

    Box(
        modifier = Modifier
            .zIndex(1f)
            .background(Green)
            .size(300.dp)
            .onGloballyPositioned { coords ->
                state.size = coords.size
                state.position = coords.positionInRoot()

                Log.d(TAG, "GreenBox: state update: ${state.toRect()}")
            },
        contentAlignment = Alignment.BottomCenter
    ) {

        Text(
            text = LoremIpsum((50)).values.joinToString(" "),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )

        AnimatedVisibility(visible = state.visibility.value) {
            Box(modifier = Modifier
                .zIndex(2f)
                .padding(bottom = 30.dp)
                .size(50.dp)
                .background(Color.Red)
                .clickable {
                    onClick(state)
                }
            )
        }
    }
}