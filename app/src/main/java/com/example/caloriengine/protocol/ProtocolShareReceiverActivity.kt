package com.example.caloriengine.protocol

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.caloriengine.protocol.data.ProtocolBufferRepository
import kotlinx.coroutines.launch

data class CaptureMovementEntry(
    var name: String,
    var cueNotes: String = ""
)

class ProtocolShareReceiverActivity : ComponentActivity() {

    private lateinit var repository: ProtocolBufferRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableTransitions()

        repository = ProtocolBufferRepository(applicationContext)

        val incomingRaw = when {
            intent?.hasExtra(Intent.EXTRA_TEXT) == true -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)
                    ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
                    ?: ""
            }
            else -> ""
        }

        val urlRegex = Regex("(https?://\\S+)")
        val urlMatch = urlRegex.find(incomingRaw)
        val extractedUrl = urlMatch?.value ?: ""
        val remainingText = incomingRaw.replace(urlRegex, "").trim()

        // Auto-detect multiple workouts if present in caption (e.g. lines, numbers "1.", "2.")
        val initialMovements = parseMovementsFromText(remainingText)

        setContent {
            QuickCaptureDialog(
                initialEntries = initialMovements,
                sourceUrl = extractedUrl,
                onStashAll = { entries, split ->
                    lifecycleScope.launch {
                        val pairs = entries.map { Pair(it.name.ifBlank { "Unorthodox Movement" }, it.cueNotes) }
                        repository.stashMultipleProtocols(
                            items = pairs,
                            sourceUrl = extractedUrl,
                            targetSplit = split
                        )
                        Toast.makeText(
                            this@ProtocolShareReceiverActivity,
                            "[ BUFFER STASHED // ${pairs.size} MOVEMENTS TO $split ]",
                            Toast.LENGTH_SHORT
                        ).show()
                        closeWithNoAnimation()
                    }
                },
                onCancel = {
                    closeWithNoAnimation()
                }
            )
        }
    }

    private fun parseMovementsFromText(text: String): List<CaptureMovementEntry> {
        if (text.isBlank()) return listOf(CaptureMovementEntry("Hanging Leg Raises", ""))

        val lines = text.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("http") }

        val detected = mutableListOf<CaptureMovementEntry>()
        for (line in lines) {
            val cleaned = line.replace(Regex("^[0-9]+[\\.\\):\\-]\\s*"), "").trim()
            if (cleaned.length in 3..60) {
                detected.add(CaptureMovementEntry(cleaned, ""))
            }
        }

        return if (detected.isNotEmpty()) detected else listOf(CaptureMovementEntry(text.take(50), ""))
    }

    private fun disableTransitions() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun closeWithNoAnimation() {
        finish()
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}

@Composable
fun QuickCaptureDialog(
    initialEntries: List<CaptureMovementEntry>,
    sourceUrl: String,
    onStashAll: (entries: List<CaptureMovementEntry>, split: String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val movements = remember {
        mutableStateListOf<CaptureMovementEntry>().apply {
            addAll(initialEntries)
        }
    }
    val splitOptions = listOf("CORE", "UPPER", "LOWER", "AUX")
    var selectedSplit by remember { mutableStateOf("UPPER") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(onClick = onCancel),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
                .background(CyberSurface)
                .border(1.dp, CyberPrimary, CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp))
                .clickable(enabled = false) {}
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "// PROTOCOL_BUFFER // INCOMING_REEL",
                    color = CyberPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                // Visual Preview Link button
                if (sourceUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(4.dp))
                            .background(Color.Black)
                            .border(1.dp, CyberWarning, CutCornerShape(4.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Cannot open video link", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "[ ↗ VIEW_REEL ]",
                            color = CyberWarning,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (sourceUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "SRC: $sourceUrl",
                    color = CyberPrimaryDim,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-Movement List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((minOf(movements.size, 3) * 115).dp.coerceIn(115.dp, 280.dp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(movements) { index, entry ->
                    var nameText by remember(entry) { mutableStateOf(entry.name) }
                    var notesText by remember(entry) { mutableStateOf(entry.cueNotes) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, CyberPrimaryDim, CutCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "> EXERCISE 0${index + 1}:",
                                color = CyberPrimaryDim,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (movements.size > 1) {
                                Text(
                                    text = "[ REMOVE ]",
                                    color = Color(0xFFFF4444),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        movements.removeAt(index)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Movement Title Field
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .border(1.dp, CyberPrimaryDim)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            BasicTextField(
                                value = nameText,
                                onValueChange = {
                                    nameText = it
                                    entry.name = it
                                },
                                textStyle = TextStyle(
                                    color = CyberPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                cursorBrush = SolidColor(CyberPrimary),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Form Cues / Unorthodox Notes Field
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .border(1.dp, CyberPrimaryDim.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            if (notesText.isEmpty()) {
                                Text(
                                    text = "Form cue / angle / variation notes...",
                                    color = CyberPrimaryDim.copy(alpha = 0.6f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            }
                            BasicTextField(
                                value = notesText,
                                onValueChange = {
                                    notesText = it
                                    entry.cueNotes = it
                                },
                                textStyle = TextStyle(
                                    color = CyberWarning,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                ),
                                cursorBrush = SolidColor(CyberWarning),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Another Movement From Reel Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        movements.add(CaptureMovementEntry("", ""))
                    }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[ + ADD ANOTHER WORKOUT FROM REEL ]",
                    color = CyberPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Split Selector
            Text(
                text = "> TARGET_SPLIT_ROUTINE:",
                color = CyberPrimaryDim,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                splitOptions.forEach { split ->
                    val isSelected = split == selectedSplit
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CutCornerShape(4.dp))
                            .background(if (isSelected) CyberPrimary else Color.Black)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CyberPrimary else CyberPrimaryDim,
                                shape = CutCornerShape(4.dp)
                            )
                            .clickable { selectedSplit = split }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = split,
                            color = if (isSelected) Color.Black else CyberPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: [ CANCEL ] and [ STASH_BUFFER ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(4.dp))
                        .background(Color.Black)
                        .border(1.dp, CyberWarning, CutCornerShape(4.dp))
                        .clickable { onCancel() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ CANCEL ]",
                        color = CyberWarning,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(CutCornerShape(4.dp))
                        .background(CyberPrimary)
                        .border(1.dp, CyberPrimary, CutCornerShape(4.dp))
                        .clickable { onStashAll(movements, selectedSplit) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (movements.size > 1) "[ STASH ALL (${movements.size}) ]" else "[ STASH_BUFFER ]",
                        color = Color.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
