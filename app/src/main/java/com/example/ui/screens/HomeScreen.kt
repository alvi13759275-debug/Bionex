package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Project
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
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val projectsState by viewModel.projectsState.collectAsState()
    var showNewProjectDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(BioDarkBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_bioscience),
                        contentDescription = "Futuristic Bioscience Laboratory Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        BioDarkBackground.copy(alpha = 0.7f),
                                        BioDarkBackground
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BioEmeraldSecondary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CORE AI ENGINES ONLINE • API 36",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = BioEmeraldSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "BioNexus Platform",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Gemini 4 Argon • Live 3.8 Audio • Firestore Persistence",
                            fontFamily = SpaceGrotesk,
                            fontSize = 12.sp,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }
            }

            // Quick Nav Action Tiles
            item {
                Text(
                    text = "LABORATORY WORKSTATIONS",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = BioCyanPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ActionTile(
                            icon = Icons.Default.Biotech,
                            title = "Genomics Lab",
                            subtitle = "Sequence & CRISPR",
                            color = BioCyanPrimary,
                            onClick = { viewModel.navigateTo("genomics") },
                            testTag = "nav_genomics_tile"
                        )
                    }
                    item {
                        ActionTile(
                            icon = Icons.Default.Science,
                            title = "Protocols",
                            subtitle = "SOPs & Bench Timers",
                            color = BioEmeraldSecondary,
                            onClick = { viewModel.navigateTo("protocols") },
                            testTag = "nav_protocols_tile"
                        )
                    }
                    item {
                        ActionTile(
                            icon = Icons.Default.Chat,
                            title = "AI Specialist",
                            subtitle = "Multi-turn Chatbot",
                            color = BioPurpleTertiary,
                            onClick = { viewModel.navigateTo("chatbot") },
                            testTag = "nav_chatbot_tile"
                        )
                    }
                    item {
                        ActionTile(
                            icon = Icons.Default.Mic,
                            title = "Live Voice",
                            subtitle = "Hands-free Bench",
                            color = BioAmberAccent,
                            onClick = { viewModel.navigateTo("voice") },
                            testTag = "nav_voice_tile"
                        )
                    }
                    item {
                        ActionTile(
                            icon = Icons.Default.Book,
                            title = "Grounding",
                            subtitle = "PubMed & Maps",
                            color = BioCyanPrimary,
                            onClick = { viewModel.navigateTo("search") },
                            testTag = "nav_search_tile"
                        )
                    }
                }
            }

            // Real-Time Bio Telemetry Metrics
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "LABORATORY TELEMETRY",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = BioCyanPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TelemetryCard(
                        title = "Containment",
                        value = "BSL-2",
                        subtitle = "Airflow Laminar",
                        color = BioEmeraldSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryCard(
                        title = "Active Model",
                        value = "Argon 4",
                        subtitle = "High Thinking ON",
                        color = BioPurpleTertiary,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryCard(
                        title = "Voice Sync",
                        value = "Live 3.8",
                        subtitle = "Low-latency TTS",
                        color = BioAmberAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Cloud Firestore Projects Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CLOUD RESEARCH PROJECTS",
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            color = BioCyanPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Synced with Cloud Firestore",
                            fontFamily = SpaceGrotesk,
                            fontSize = 12.sp,
                            color = Color(0xFF78909C)
                        )
                    }
                    IconButton(
                        onClick = { showNewProjectDialog = true },
                        modifier = Modifier.testTag("add_project_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Project",
                            tint = BioCyanPrimary
                        )
                    }
                }
            }

            when (val state = projectsState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BioCyanPrimary, modifier = Modifier.size(28.dp))
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Text(
                            text = "Failed to load projects: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            fontFamily = SpaceGrotesk,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        item {
                            EmptyProjectsCard(onAddClicked = { showNewProjectDialog = true })
                        }
                    } else {
                        items(state.data) { project ->
                            ProjectCard(
                                project = project,
                                onDelete = { viewModel.deleteProject(project.id) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showNewProjectDialog = true },
            containerColor = BioCyanPrimary,
            contentColor = BioDarkBackground,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
                .testTag("fab_new_project")
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Project")
        }

        if (showNewProjectDialog) {
            NewProjectDialog(
                onDismiss = { showNewProjectDialog = false },
                onSave = { title, category, summary, content, tags ->
                    viewModel.saveNewProject(title, category, summary, content, tags)
                    showNewProjectDialog = false
                }
            )
        }
    }
}

@Composable
private fun ActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = Color(0xFF90A4AE)
            )
        }
    }
}

@Composable
private fun TelemetryCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title.uppercase(),
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = Color(0xFF78909C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
            Text(
                text = subtitle,
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                color = Color(0xFF90A4AE)
            )
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BioCyanPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = project.category.uppercase(),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BioCyanPrimary
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete project",
                        tint = Color(0xFF78909C),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            if (project.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.summary,
                    fontFamily = SpaceGrotesk,
                    fontSize = 13.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 18.sp
                )
            }

            if (project.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    project.tags.take(3).forEach { tag ->
                        Text(
                            text = "#$tag",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = BioEmeraldSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProjectsCard(onAddClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BioDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BioDarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Biotech,
                contentDescription = null,
                tint = BioCyanPrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No Cloud Projects Yet",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = "Start by drafting your first genomics experiment or drug target investigation.",
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = Color(0xFF90A4AE),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAddClicked,
                colors = ButtonDefaults.buttonColors(containerColor = BioCyanPrimary, contentColor = BioDarkBackground),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Create First Project", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NewProjectDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, summary: String, content: String, tags: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Genomics") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("CRISPR, Target, Assay") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BioDarkSurface,
        title = {
            Text(
                text = "New Research Study",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BioCyanPrimary,
                        unfocusedBorderColor = BioDarkBorder
                    )
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Genomics, Proteomics)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BioCyanPrimary,
                        unfocusedBorderColor = BioDarkBorder
                    )
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Abstract / Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BioCyanPrimary,
                        unfocusedBorderColor = BioDarkBorder
                    )
                )
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BioCyanPrimary,
                        unfocusedBorderColor = BioDarkBorder
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onSave(title, category, summary, content, tags)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BioCyanPrimary, contentColor = BioDarkBackground)
            ) {
                Text("Save to Cloud")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF90A4AE))
            }
        }
    )
}
