package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Protocol
import com.example.ui.MainViewModel
import com.example.ui.UiState
import com.example.ui.theme.BioAmberAccent
import com.example.ui.theme.BioCyanPrimary
import com.example.ui.theme.BioDarkBackground
import com.example.ui.theme.BioDarkBorder
import com.example.ui.theme.BioDarkSurface
import com.example.ui.theme.BioDarkSurfaceVariant
import com.example.ui.theme.BioEmeraldSecondary
import com.example.ui.theme.BioPurpleTertiary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import kotlinx.coroutines.delay

@Composable
fun ProtocolScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val protocolsState by viewModel.protocolsState.collectAsState()
    var selectedProtocol by remember { mutableStateOf<Protocol?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Active bench step runner state
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var timerSeconds by remember { mutableIntStateOf(300) } // 5 minutes default
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
        }
        if (timerSeconds == 0 && isTimerRunning) {
            isTimerRunning = false
            viewModel.speakProtocolStep("Timer completed for current experiment incubation.")
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BioDarkBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BioEmeraldSecondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Protocols",
                                tint = BioEmeraldSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Standard Operating Protocols (SOP)",
                                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
                            )
                            Text(
                                text = "Voice-assisted Step Execution & Reagent Benchmarking",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = BioEmeraldSecondary
                            )
                        }
                    }
                }

                // Bench Runner Active Card (if a protocol is selected)
                if (selectedProtocol != null) {
                    val proto = selectedProtocol!!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BioEmeraldSecondary)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "ACTIVE BENCH EXECUTION",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            color = BioEmeraldSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = proto.name,
                                            fontFamily = SpaceGrotesk,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                    }
                                    IconButton(
                                        onClick = { selectedProtocol = null }
                                    ) {
                                        Text("Close", color = Color(0xFF90A4AE), fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Active Timer Display
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BioDarkSurfaceVariant)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "INCUBATION TIMER",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 10.sp,
                                            color = Color(0xFF90A4AE)
                                        )
                                        val minutes = timerSeconds / 60
                                        val seconds = timerSeconds % 60
                                        Text(
                                            text = "%02d:%02d".format(minutes, seconds),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (timerSeconds < 30) BioAmberAccent else BioCyanPrimary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { isTimerRunning = !isTimerRunning },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isTimerRunning) BioAmberAccent else BioEmeraldSecondary,
                                                contentColor = BioDarkBackground
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isTimerRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isTimerRunning) "Pause" else "Start")
                                        }
                                        IconButton(
                                            onClick = {
                                                isTimerRunning = false
                                                timerSeconds = 300
                                            }
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Reset timer", tint = Color(0xFF90A4AE))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Current Step with TTS Narration
                                val steps = proto.steps
                                if (steps.isNotEmpty() && currentStepIndex < steps.size) {
                                    val currentStepText = steps[currentStepIndex]
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = BioDarkBackground),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "STEP ${currentStepIndex + 1} OF ${steps.size}",
                                                    fontFamily = JetBrainsMono,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BioCyanPrimary
                                                )
                                                // TTS Button to read step aloud (hands-free laboratory requirement)
                                                Button(
                                                    onClick = { viewModel.speakProtocolStep("Step ${currentStepIndex + 1}: $currentStepText") },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = BioCyanPrimary.copy(alpha = 0.2f),
                                                        contentColor = BioCyanPrimary
                                                    ),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("tts_read_step_button")
                                                ) {
                                                    Icon(Icons.Default.VolumeUp, contentDescription = "Read aloud", modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Read Aloud (TTS)", fontSize = 11.sp)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = currentStepText,
                                                fontFamily = SpaceGrotesk,
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                                            enabled = currentStepIndex > 0,
                                            colors = ButtonDefaults.buttonColors(containerColor = BioDarkSurfaceVariant),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Previous Step")
                                        }
                                        Button(
                                            onClick = {
                                                if (currentStepIndex < steps.size - 1) {
                                                    currentStepIndex++
                                                    viewModel.speakProtocolStep("Proceeding to Step ${currentStepIndex + 1}: ${steps[currentStepIndex]}")
                                                }
                                            },
                                            enabled = currentStepIndex < steps.size - 1,
                                            colors = ButtonDefaults.buttonColors(containerColor = BioEmeraldSecondary, contentColor = BioDarkBackground),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Next Step")
                                        }
                                    }
                                }

                                // Safety Warnings
                                if (proto.safetyNotes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = BioAmberAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = proto.safetyNotes,
                                            fontFamily = SpaceGrotesk,
                                            fontSize = 11.sp,
                                            color = BioAmberAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Protocol List
                item {
                    Text(
                        text = "REGISTERED LABORATORY PROCEDURES",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = BioCyanPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                when (val state = protocolsState) {
                    is UiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = BioCyanPrimary, modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                            Text(
                                text = "Failed to load protocols: ${state.message}",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            item {
                                EmptyProtocolsCard(onCreate = { showCreateDialog = true })
                            }
                        } else {
                            itemsIndexed(state.data) { _, protocol ->
                                ProtocolCard(
                                    protocol = protocol,
                                    isSelected = selectedProtocol?.id == protocol.id,
                                    onSelect = {
                                        selectedProtocol = protocol
                                        currentStepIndex = 0
                                        timerSeconds = 300
                                        isTimerRunning = false
                                    },
                                    onDelete = { viewModel.deleteProtocol(protocol.id) }
                                )
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = BioEmeraldSecondary,
                contentColor = BioDarkBackground,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 20.dp)
                    .testTag("fab_new_protocol")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Protocol")
            }

            if (showCreateDialog) {
                NewProtocolDialog(
                    onDismiss = { showCreateDialog = false },
                    onSave = { name, category, reagents, steps, safety ->
                        viewModel.saveNewProtocol(name, category, reagents, steps, safety)
                        showCreateDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ProtocolCard(
    protocol: Protocol,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BioDarkSurfaceVariant else BioDarkSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) BioEmeraldSecondary else BioDarkBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BioEmeraldSecondary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = protocol.category.uppercase(),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BioEmeraldSecondary
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF78909C), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = protocol.name,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${protocol.steps.size} steps • ${protocol.reagents.size} reagents",
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = Color(0xFF90A4AE)
            )
        }
    }
}

@Composable
private fun EmptyProtocolsCard(onCreate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Science, contentDescription = null, tint = BioEmeraldSecondary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("No SOP Protocols Found", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                "Create a bench standard operating protocol with timed incubation steps and reagent checklists.",
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = Color(0xFF90A4AE),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            Button(
                onClick = onCreate,
                colors = ButtonDefaults.buttonColors(containerColor = BioEmeraldSecondary, contentColor = BioDarkBackground)
            ) {
                Text("Create First Protocol")
            }
        }
    }
}

@Composable
private fun NewProtocolDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, reagents: List<String>, steps: List<String>, safety: String) -> Unit
) {
    var name by remember { mutableStateOf("Western Blot SDS-PAGE Protocol") }
    var category by remember { mutableStateOf("Proteomics") }
    var reagentsInput by remember { mutableStateOf("Tris-HCl, SDS, Polyacrylamide, TBS-T, PVDF Membrane") }
    var stepsInput by remember { mutableStateOf("Prepare resolving gel (10%), Load protein lysate (20 ug/lane), Run at 120V for 90 minutes, Transfer to PVDF membrane at 100V 60m, Block with 5% non-fat milk for 1h") }
    var safety by remember { mutableStateOf("Neurotoxin warning: handle unpolymerized acrylamide in fume hood with double gloves.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BioDarkSurface,
        title = {
            Text("Create Lab Protocol (SOP)", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Protocol Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BioEmeraldSecondary, unfocusedBorderColor = BioDarkBorder)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BioEmeraldSecondary, unfocusedBorderColor = BioDarkBorder)
                )
                OutlinedTextField(
                    value = reagentsInput,
                    onValueChange = { reagentsInput = it },
                    label = { Text("Reagents (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BioEmeraldSecondary, unfocusedBorderColor = BioDarkBorder)
                )
                OutlinedTextField(
                    value = stepsInput,
                    onValueChange = { stepsInput = it },
                    label = { Text("Steps (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BioEmeraldSecondary, unfocusedBorderColor = BioDarkBorder)
                )
                OutlinedTextField(
                    value = safety,
                    onValueChange = { safety = it },
                    label = { Text("Biosafety / PPE Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BioEmeraldSecondary, unfocusedBorderColor = BioDarkBorder)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val reagents = reagentsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        val steps = stepsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onSave(name, category, reagents, steps, safety)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BioEmeraldSecondary, contentColor = BioDarkBackground)
            ) {
                Text("Save SOP")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF90A4AE)) }
        }
    )
}
