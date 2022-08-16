package com.example.dragableview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier
                                .zIndex(2f),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            (1..3).forEach {
                                DraggableView(
                                    state = DraggableViewState(Offset.Zero, Offset.Zero, mutableStateOf(true), "ABC".random()),
                                    onDragEnd = viewModel::checkCardsForIntersectionWithView,
                                    addStateToList = viewModel::addDraggableViewState
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            modifier = Modifier
                                .horizontalScroll(state = ScrollState(0))
                        ) {
                            (0..5).forEach {
                                CardBox(
                                    state = CardBoxState(Offset.Zero, IntSize.Zero),
                                    addState = viewModel::addGreenBoxState,
                                    onClick = viewModel::onCardClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableView(
    state: DraggableViewState,
    onDragEnd: (DraggableViewState) -> Boolean,
    addStateToList: (DraggableViewState) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var position by remember { mutableStateOf(Offset.Zero) }
    val animatedOffset by remember {
        mutableStateOf(Animatable(state.startOffset, Offset.VectorConverter))
    }

    LaunchedEffect(Unit) {
        addStateToList(state)
    }

    Box {
        if (state.visibility.value) {
            Box(
                modifier = Modifier
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
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                state.currentOffset = position
                                val isCardArea = onDragEnd(state)
                                if (isCardArea) {
                                    coroutineScope.launch {
                                        animatedOffset.snapTo(targetValue = state.startOffset)
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

                    },
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = state.content.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 32.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .border(width = 1.dp, color = Blue.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center

        ) {
            Text(
                text = state.content.toString(),
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                fontSize = 32.sp
            )
        }
    }
}

@Composable
fun CardBox(
    state: CardBoxState,
    addState: (CardBoxState) -> Unit,
    onClick: (CardBoxState) -> Unit
) {
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

        AnimatedVisibility(visible = state.chipIsVisible()) {
            Box(
                modifier = Modifier
                    .zIndex(2f)
                    .padding(bottom = 30.dp)
                    .size(50.dp)
                    .background(Color.Red)
                    .clickable {
                        onClick(state)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.getStickiedContent(),
                    textAlign = TextAlign.Center,
                    color = if (state.chipIsVisible()) Color.White else Color.Transparent,
                    fontSize = 32.sp
                )
            }
        }
    }
}