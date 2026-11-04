package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.MainViewModel
import com.example.ui.theme.BioAmberAccent
import com.example.ui.theme.BioCyanPrimary
import com.example.ui.theme.BioDarkBackground
import com.example.ui.theme.BioDarkBorder
import com.example.ui.theme.BioDarkSurface
import com.example.ui.theme.BioDarkSurfaceVariant
import com.example.ui.theme.BioEmeraldSecondary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.voice.VoiceState

@Composable
fun VoiceLiveScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val voiceState by viewModel.voiceAssistant.voiceState.collectAsState()
    val transcript by viewModel.liveAudioTranscript.collectAsState()
    val responseText by viewModel.liveAudioResponse.collectAsState()
    val waveform by viewModel.voiceAssistant.audioWaveform.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            viewModel.voiceAssistant.startListening()
        }
    }

    val pulseScale = remember { Animatable(1.0f) }
    LaunchedEffect(voiceState) {
        if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) {
            pulseScale.animateTo(
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseScale.snapTo(1.0f)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BioDarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BioAmberAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Live",
                        tint = BioAmberAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Hands-Free Lab Voice Copilot",
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
                    )
                    Text(
                        text = "Real-time Live Audio Conversation with gemini-3.8-live",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = BioAmberAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Central Pulsating Mic Sphere
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(pulseScale.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    when (voiceState) {
                                        VoiceState.LISTENING -> BioAmberAccent.copy(alpha = 0.4f)
                                        VoiceState.SPEAKING -> BioEmeraldSecondary.copy(alpha = 0.4f)
                                        VoiceState.PROCESSING -> BioCyanPrimary.copy(alpha = 0.4f)
                                        else -> BioDarkSurfaceVariant.copy(alpha = 0.5f)
                                    },
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Main Circle Button
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            when (voiceState) {
                                VoiceState.LISTENING -> BioAmberAccent
                                VoiceState.SPEAKING -> BioEmeraldSecondary
                                VoiceState.PROCESSING -> BioCyanPrimary
                                else -> BioDarkSurface
                            }
                        )
                        .border(
                            2.dp,
                            when (voiceState) {
                                VoiceState.LISTENING -> BioAmberAccent
                                VoiceState.SPEAKING -> BioEmeraldSecondary
                                else -> BioDarkBorder
                            },
                            CircleShape
                        )
                        .clickable {
                            if (!hasAudioPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                if (voiceState == VoiceState.LISTENING) {
                                    viewModel.voiceAssistant.stopListening()
                                } else if (voiceState == VoiceState.SPEAKING) {
                                    viewModel.voiceAssistant.stopSpeaking()
                                } else {
                                    viewModel.voiceAssistant.startListening()
                                }
                            }
                        }
                        .testTag("voice_mic_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (voiceState) {
                            VoiceState.LISTENING -> Icons.Default.Mic
                            VoiceState.SPEAKING -> Icons.Default.VolumeUp
                            VoiceState.PROCESSING -> Icons.Default.Science
                            else -> Icons.Default.MicOff
                        },
                        contentDescription = "Microphone",
                        tint = if (voiceState == VoiceState.IDLE) BioCyanPrimary else BioDarkBackground,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Voice State Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BioDarkSurface)
                    .border(1.dp, BioDarkBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when (voiceState) {
                        VoiceState.LISTENING -> "LISTENING TO LAB OBSERVATION..."
                        VoiceState.SPEAKING -> "GEMINI LIVE SPEAKING VIA TTS..."
                        VoiceState.PROCESSING -> "PROCESSING WITH GEMINI-3.8-LIVE..."
                        VoiceState.ERROR -> "AUDIO CAPTURE ERROR"
                        else -> "TAP MICROPHONE TO SPEAK"
                    },
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (voiceState) {
                        VoiceState.LISTENING -> BioAmberAccent
                        VoiceState.SPEAKING -> BioEmeraldSecondary
                        VoiceState.PROCESSING -> BioCyanPrimary
                        else -> Color(0xFF90A4AE)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Waveform visualizer bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                waveform.forEach { amplitude ->
                    val barHeight = (36 * amplitude).dp.coerceAtLeast(4.dp)
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (voiceState == VoiceState.LISTENING) BioAmberAccent else BioCyanPrimary
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Live Transcript Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "OBSERVED SPEECH TRANSCRIPT",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BioCyanPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = transcript.ifEmpty { "Say e.g. 'What is the optimal annealing temperature for this 24bp primer?' or 'Check BSL protocol for lentivirus.'" },
                        fontFamily = SpaceGrotesk,
                        fontSize = 13.sp,
                        color = if (transcript.isEmpty()) Color(0xFF546E7A) else Color.White,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Response Card with TTS playback
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BioEmeraldSecondary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GEMINI-3.8-LIVE SPOKEN RESPONSE",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BioEmeraldSecondary
                        )
                        IconButton(
                            onClick = { viewModel.voiceAssistant.speak(responseText) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Replay audio",
                                tint = BioEmeraldSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = responseText,
                        fontFamily = SpaceGrotesk,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Prompt Suggestions
            Text(
                text = "VOICE COPILOT BENCH SHORTCUTS",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = Color(0xFF78909C),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VoiceShortcutChip("Calculate 10% SDS solution recipe for 500 mL") {
                    viewModel.handleLiveVoiceQuery("Calculate 10% SDS solution recipe for 500 mL")
                }
                VoiceShortcutChip("What are the PPE safety rules for BSL-3 laboratory?") {
                    viewModel.handleLiveVoiceQuery("What are the PPE safety rules for BSL-3 laboratory?")
                }
                VoiceShortcutChip("Explain CRISPR SpCas9 PAM site recognition kinetics") {
                    viewModel.handleLiveVoiceQuery("Explain CRISPR SpCas9 PAM site recognition kinetics")
                }
            }
        }
    }
}

@Composable
private fun VoiceShortcutChip(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BioDarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = BioCyanPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "\"$text\"",
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = Color(0xFFCFD8DC)
            )
        }
    }
}
