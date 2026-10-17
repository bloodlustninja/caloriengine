package com.example.caloriengine.protocol

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

// Cyberpunk / Terminal Color Tokens
val CyberBg = Color(0xFF0A0D0B)
val CyberSurface = Color(0xFF121814)
val CyberPrimary = Color(0xFF00FF66)
val CyberPrimaryDim = Color(0xFF1F3D2B)
val CyberWarning = Color(0xFFFFAA00)

@Composable
fun ProtocolScreen(
    viewModel: ProtocolViewModel
) {
    val state by viewModel.uiState.collectAsState()

    ProtocolScreenContent(
        state = state,
        onQuoteTapped = viewModel::onQuoteTapped,
        onDaySelected = viewModel::onDaySelected,
        onUpdateSplit = viewModel::onUpdateSplit,
        onToggleExercise = viewModel::onToggleExercise,
        onExecuteProtocol = viewModel::onExecuteProtocol
    )
}

@Composable
fun ProtocolScreenContent(
    state: ProtocolUiState,
    onQuoteTapped: () -> Unit = {},
    onDaySelected: (Int) -> Unit = {},
    onUpdateSplit: (Int, SplitType) -> Unit = { _, _ -> },
    onToggleExercise: (String) -> Unit = {},
    onExecuteProtocol: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CyberBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CALORIENGINE // PROTOCOL_HUB",
                        color = CyberPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SYS_ONLINE",
                        color = CyberPrimaryDim,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }

            // A. Hero Header & NPC Dialogue Box
            item {
                HeroBonfireSection(
                    quote = state.currentQuote,
                    onQuoteTapped = onQuoteTapped
                )
            }

            // B. 7-Day Split Selector Strip
            item {
                WeeklySplitSelectorStrip(
                    currentDayIdx = state.currentDayIndex,
                    selectedDayIdx = state.selectedDayIndex,
                    weeklySplit = state.weeklySplit,
                    onDaySelected = onDaySelected,
                    onUpdateSplit = onUpdateSplit
                )
            }

            // C. Today's Protocol Dynamic Card
            item {
                val selectedSplit = state.weeklySplit[state.selectedDayIndex] ?: SplitType.REST
                
                ProtocolDynamicCard(
                    splitType = selectedSplit,
                    checklist = state.todayChecklist,
                    isCompleted = state.isCompleted,
                    onToggleExercise = onToggleExercise,
                    onExecuteProtocol = onExecuteProtocol
                )
            }
        }
    }
}

@Composable
fun HeroBonfireSection(
    quote: String,
    onQuoteTapped: () -> Unit
) {
    val context = LocalContext.current
    
    // Coil ImageLoader configured for animated GIFs
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    // Load animated GIF or fallback image
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/cyberpunk_bonfire.gif")
            .crossfade(true)
            .build(),
        imageLoader = imageLoader
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
            .background(CyberSurface)
            .border(1.dp, CyberPrimaryDim, CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bonfire Hero Container Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color.Black)
                .border(1.dp, CyberPrimaryDim),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = "Cyberpunk Bonfire",
                modifier = Modifier.fillMaxSize()
            )
            
            // Retro Scanline Text Tag Overlay
            Text(
                text = "NEO_BONFIRE // RECOVERY_NODE",
                color = CyberPrimary.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Retro Dialogue Box
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alphaAnim by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.8f))
                .border(1.dp, CyberPrimary)
                .clickable { onQuoteTapped() }
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "> KINETIC_OPERATIVE // QUOTE_OF_THE_DAY",
                    color = CyberPrimaryDim,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"$quote\"",
                    color = CyberPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.alpha(alphaAnim)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "[ TAP TO CYCLE TRANSMISSION ]",
                    color = CyberWarning,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun WeeklySplitSelectorStrip(
    currentDayIdx: Int,
    selectedDayIdx: Int,
    weeklySplit: Map<Int, SplitType>,
    onDaySelected: (Int) -> Unit,
    onUpdateSplit: (Int, SplitType) -> Unit
) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(12.dp))
            .background(CyberSurface)
            .border(1.dp, CyberPrimaryDim, CutCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "7-DAY SPLIT PROTOCOL // SELECT DAY TO REASSIGN",
            color = CyberPrimaryDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayLabels.forEachIndexed { index, label ->
                val isToday = index == currentDayIdx
                val isSelected = index == selectedDayIdx
                val split = weeklySplit[index] ?: SplitType.REST
                var showMenu by remember { mutableStateOf(false) }

                Box {
                    Column(
                        modifier = Modifier
                            .width(42.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) CyberPrimary.copy(alpha = 0.15f) else Color.Black
                            )
                            .border(
                                width = if (isToday) 2.dp else 1.dp,
                                color = if (isToday) CyberPrimary else if (isSelected) CyberPrimary.copy(alpha = 0.6f) else CyberPrimaryDim,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                onDaySelected(index)
                                showMenu = true
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            color = if (isToday) CyberPrimary else Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = split.name.take(3),
                            color = if (split == SplitType.REST) CyberWarning else CyberPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Split Dropdown Reassignment Menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(CyberSurface).border(1.dp, CyberPrimary)
                    ) {
                        SplitType.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "[ ${option.name} ]",
                                        color = CyberPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                },
                                onClick = {
                                    onUpdateSplit(index, option)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProtocolDynamicCard(
    splitType: SplitType,
    checklist: List<ProtocolExercise>,
    isCompleted: Boolean,
    onToggleExercise: (String) -> Unit,
    onExecuteProtocol: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
            .background(CyberSurface)
            .border(1.dp, if (isCompleted) CyberPrimary else CyberPrimaryDim, CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
            .padding(16.dp)
    ) {
        // Card Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TODAY'S PROTOCOL // ${splitType.name}",
                color = CyberPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            if (isCompleted) {
                Text(
                    text = "[ EXECUTED ]",
                    color = CyberPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (splitType == SplitType.REST) {
            // Rest Day Ambient Recovery Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, CyberWarning)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "[ STATUS: RECOVERY // CELLULAR REPAIR ACTIVE ]",
                        color = CyberWarning,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calorie target locked to baseline maintenance. No kinetic expenditure required today. Rebuild.",
                        color = CyberPrimaryDim,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        } else {
            // Training Day Routine Checklist
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                checklist.forEach { exercise ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, CyberPrimaryDim)
                            .clickable { onToggleExercise(exercise.id) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = exercise.isDone,
                            onCheckedChange = { onToggleExercise(exercise.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CyberPrimary,
                                uncheckedColor = CyberPrimaryDim,
                                checkmarkColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.name,
                                color = if (exercise.isDone) CyberPrimaryDim else CyberPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = exercise.setsReps,
                                color = CyberPrimaryDim,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Execute Button
                Button(
                    onClick = onExecuteProtocol,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCompleted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberPrimary,
                        contentColor = Color.Black,
                        disabledContainerColor = CyberPrimaryDim,
                        disabledContentColor = Color.Gray
                    ),
                    shape = CutCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isCompleted) "[ PROTOCOL COMPLETED (-300 KCAL) ]" else "[ EXECUTE PROTOCOL / COMPLETE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProtocolScreenPreview() {
    ProtocolScreenContent(
        state = ProtocolUiState()
    )
}
