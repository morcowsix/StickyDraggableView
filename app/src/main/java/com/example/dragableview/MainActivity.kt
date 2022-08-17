package com.example.dragableview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.dragableview.ui.theme.DragableViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: StickyViewModel by viewModels()
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
                                DraggableStickyView(
                                    state = DraggableViewState(Offset.Zero, Offset.Zero, mutableStateOf(true), "ABC".random()),
                                    checkIntersection = viewModel::checkCardsForIntersection,
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
                                val state = StickyHolderState(Offset.Zero, IntSize.Zero)
                                StickyHolder(
                                    modifier = Modifier
                                        .background(Color.Green)
                                        .size(300.dp),
                                    state = state,
                                    rememberState = viewModel::addStickyHolderState,
                                ) {
                                    Text(
                                        text = LoremIpsum((50)).values.joinToString(" "),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 22.sp
                                    )

                                    Sticker(
                                        visible = state.stickerIsVisible()
                                    ) {
//                                        Button(
//                                            onClick = { viewModel.onCardClick(state) },
//                                            modifier = Modifier
//                                                .padding(bottom = 30.dp)
//                                        ) {
//                                            Text(text = "button1")
//                                        }
                                        Text(
                                            text = state.getStickiedContent(),
                                            textAlign = TextAlign.Center,
                                            color = if (state.stickerIsVisible()) Color.White else Color.Transparent,
                                            fontSize = 32.sp,
                                            modifier = Modifier
                                                .padding(bottom = 30.dp)
                                                .size(50.dp)
                                                .background(Color.Red)
                                                .clickable { viewModel.onStickerClick(state) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}