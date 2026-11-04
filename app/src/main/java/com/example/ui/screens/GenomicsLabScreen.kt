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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun GenomicsLabScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val sequence by viewModel.sequenceInput.collectAsState()
    val analysis = viewModel.sequenceAnalysis.collectAsState().value
    val highThinkingState by viewModel.highThinkingState.collectAsState()

    var variantDetails by remember { mutableStateOf("c.1799T>A (p.V600E) kinase domain substitution") }
    var diseaseContext by remember { mutableStateOf("Metastatic Melanoma / BRAF inhibitor resistance") }
    var isThinkingExpanded by remember { mutableStateOf(true) }

    val clipboardManager = LocalClipboardManager.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BioDarkBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BioCyanPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Biotech,
                            contentDescription = "Genomics Lab",
                            tint = BioCyanPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Genomics & Variant Reasoning",
                            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
                        )
                        Text(
                            text = "Powered by Gemini 4 Argon High-Thinking Architecture",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = BioEmeraldSecondary
                        )
                    }
                }
            }

            // DNA/RNA Sequence Input Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TARGET NUCLEOTIDE SEQUENCE",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BioCyanPrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.updateSequence(
                                            "ATGGCGGCGCTGAGCGGTGGCGGCGGTGGCAGCAGCGGCGGCGGCGGCGGCGGCGGCGGC" +
                                            "GGAGGCGAGCAGTGGACAGCGCTACTCGAGCTGCAGGAGCAGCTGAACAAGCTGTTCCTG" +
                                            "GAGATGCGGCACGAGCTGCTGAGCCAGCACGAGGCCGAGGTGAACCAGCAGCTGAACGAG"
                                        )
                                        viewModel.analyzeCurrentSequence()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BioDarkSurfaceVariant,
                                        contentColor = BioCyanPrimary
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Load Sample FASTA", fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = sequence,
                            onValueChange = {
                                viewModel.updateSequence(it)
                                viewModel.analyzeCurrentSequence()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .testTag("sequence_input_field"),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = Color(0xFFE0F7FA),
                                lineHeight = 18.sp
                            ),
                            placeholder = { Text("Paste 5'->3' DNA/RNA sequence...", color = Color(0xFF546E7A)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BioCyanPrimary,
                                unfocusedBorderColor = BioDarkBorder
                            )
                        )
                    }
                }
            }

            // Real-time Biophysical Stats Grid
            if (analysis != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SEQUENCE METRICS & PHYSICOCHEMICAL PROFILE",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BioEmeraldSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox("Length", "${analysis.sequenceLength} bp", BioCyanPrimary, Modifier.weight(1f))
                            StatBox("GC Content", "${"%.1f".format(analysis.gcContent)}%", BioEmeraldSecondary, Modifier.weight(1f))
                            StatBox("AT Content", "${"%.1f".format(analysis.atContent)}%", BioAmberAccent, Modifier.weight(1f))
                            StatBox("Tm (Est)", "${"%.1f".format(analysis.meltingTempCelsius)}°C", BioPurpleTertiary, Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox("SpCas9 PAM", "${analysis.crisprPamSitesCount} sites", BioCyanPrimary, Modifier.weight(1f))
                            StatBox("Open Reading Frames", "${analysis.openReadingFramesCount} ORFs", BioEmeraldSecondary, Modifier.weight(1f))
                        }
                    }
                }

                // Translated Amino Acid Sequence
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TRANSLATED POLYPEPTIDE RESIDUES",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BioCyanPrimary
                                )
                                IconButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(analysis.translatedAminoAcids)) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF90A4AE), modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = analysis.translatedAminoAcids.ifEmpty { "No open codons" },
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = BioEmeraldSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // High Thinking Mode Section (Gemini 4 Argon)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BioPurpleTertiary.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = BioPurpleTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "GEMINI 4 ARGON HIGH-THINKING SUITE",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BioPurpleTertiary
                                )
                                Text(
                                    text = "Multi-step chain-of-thought variant & structural reasoning",
                                    fontFamily = SpaceGrotesk,
                                    fontSize = 11.sp,
                                    color = Color(0xFFB0BEC5)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = variantDetails,
                            onValueChange = { variantDetails = it },
                            label = { Text("Mutation / Variant Notation (HGVS)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BioPurpleTertiary,
                                unfocusedBorderColor = BioDarkBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = diseaseContext,
                            onValueChange = { diseaseContext = it },
                            label = { Text("Clinical Phenotype / Disease Model") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BioPurpleTertiary,
                                unfocusedBorderColor = BioDarkBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.runHighThinkingArgonAnalysis(variantDetails, diseaseContext) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_high_thinking_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BioPurpleTertiary,
                                contentColor = BioDarkBackground
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Engage High Thinking Reasoning", fontWeight = FontWeight.Bold)
                        }

                        // High Thinking Output Render
                        when (val state = highThinkingState) {
                            is UiState.Loading -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        color = BioPurpleTertiary,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Simulating protein folding & kinetic impact...",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 12.sp,
                                        color = BioPurpleTertiary
                                    )
                                }
                            }
                            is UiState.Error -> {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Reasoning Failed: ${state.message}",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                            is UiState.Success -> {
                                val (thinking, assessment) = state.data
                                if (assessment.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Collapsible Thinking Process Box
                                    if (thinking.isNotBlank()) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = BioDarkSurfaceVariant),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { isThinkingExpanded = !isThinkingExpanded },
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Psychology,
                                                            contentDescription = null,
                                                            tint = BioAmberAccent,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "INTERNAL CHAIN-OF-THOUGHT TRACE",
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = BioAmberAccent
                                                        )
                                                    }
                                                    Icon(
                                                        imageVector = if (isThinkingExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = BioAmberAccent
                                                    )
                                                }
                                                AnimatedVisibility(visible = isThinkingExpanded) {
                                                    Text(
                                                        text = thinking,
                                                        fontFamily = JetBrainsMono,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFFCFD8DC),
                                                        lineHeight = 16.sp,
                                                        modifier = Modifier.padding(top = 8.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    // Final Bioscience Assessment
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = BioDarkSurfaceVariant),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BioEmeraldSecondary.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(
                                                text = "VERIFIED BIOSCIENCE CONCLUSION",
                                                fontFamily = JetBrainsMono,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BioEmeraldSecondary
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = assessment,
                                                fontFamily = SpaceGrotesk,
                                                fontSize = 13.sp,
                                                color = Color.White,
                                                lineHeight = 20.sp
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
}

@Composable
private fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                color = Color(0xFF78909C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = color
            )
        }
    }
}
