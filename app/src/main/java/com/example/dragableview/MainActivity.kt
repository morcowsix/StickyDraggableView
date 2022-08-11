package com.example.dragableview

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.example.dragableview.ui.theme.DragableViewTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
                        DragView(
                            state = viewModel.dragViewState,
                            onDragEnd = viewModel::onDragEnd,
                            onOffsetChange = viewModel::onOffsetChange,
                            testState = viewModel.testState
                        )
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
                                GreenBox(GreenBoxState(Offset.Zero, IntSize.Zero), viewModel::addState, viewModel::setPosition)
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
fun AnimatableDragView() {
    val coroutineScope = rememberCoroutineScope()
    val offsetY  =  remember { Animatable(0f) }
    val offsetX  =  remember { Animatable(0f) }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f)
    ) {
        Surface(
            color = Color(0xFF34AB52),
            modifier = Modifier
                .size(100.dp)
                .offset {
                    IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
                }
                .draggable(
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            offsetY.snapTo(offsetY.value + delta)
                            offsetX.snapTo(offsetX.value + delta)
                        }
                    },
                    orientation = Orientation.Vertical,
                    onDragStopped = {
                        coroutineScope.launch {
                            offsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 3000,
                                    delayMillis = 0
                                )
                            )
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 3000,
                                    delayMillis = 0
                                )
                            )
                        }
                    }
                )
        ) {
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
        mutableStateOf(Animatable(Offset(0f, 0f), Offset.VectorConverter))
    }


    Box(
        Modifier
            .offset {
                IntOffset(
                    animatedOffset.value.x.roundToInt(),
                    animatedOffset.value.y.roundToInt(),
                )
            }
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
                        val isNotCardArea = onDragEnd(position)
                        if (isNotCardArea) {
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
                        animatedOffset.snapTo(Offset(
                            animatedOffset.value.x + dragAmount.x,
                            animatedOffset.value.y + dragAmount.y,
                        ))
                    }
                }
            }

    )
}

@Composable
fun GreenBox(
    state: GreenBoxState,
    addState: (GreenBoxState) -> Unit,
    setPosition: (Rect) -> Unit
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
            }
//            .pointerInteropFilter { motionEvent ->
//                val xCoord: Float = motionEvent.x
//                val yCoord: Float = motionEvent.y
//
//                Log.d(TAG, "GreenBox: event: $motionEvent")
//
//                when (motionEvent.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        Log.d(TAG, "GreenBox: down: $xCoord, $yCoord")
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        Log.d(TAG, "GreenBox: move: $xCoord, $yCoord")
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        Log.d(TAG, "GreenBox: up: $xCoord, $yCoord")
//                    }
//                    else -> {
//                        Log.d(TAG, "GreenBox: default: $xCoord, $yCoord")
//                        false
//                    }
//                }
//                true
//
//            }

            
    )
}