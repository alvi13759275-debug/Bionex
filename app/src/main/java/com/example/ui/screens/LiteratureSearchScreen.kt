package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BiotechLocationItem
import com.example.data.model.LiteratureItem
import com.example.ui.MainViewModel
import com.example.ui.UiState
import com.example.ui.theme.BioCyanPrimary
import com.example.ui.theme.BioDarkBackground
import com.example.ui.theme.BioDarkBorder
import com.example.ui.theme.BioDarkSurface
import com.example.ui.theme.BioDarkSurfaceVariant
import com.example.ui.theme.BioEmeraldSecondary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk

@Composable
fun LiteratureSearchScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val searchResults by viewModel.literatureResults.collectAsState()
    val locationResults by viewModel.biotechLocations.collectAsState()

    var searchQuery by remember { mutableStateOf("CRISPR prime editing cancer therapeutics") }
    var locationCity by remember { mutableStateOf("Boston") }

    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BioDarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BioCyanPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Default.Book else Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BioCyanPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (selectedTab == 0) "Google Search Grounding" else "Google Maps Grounding",
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
                    )
                    Text(
                        text = if (selectedTab == 0) "Live PubMed, Nature & Clinical Trials (gemini-3.8-flash)" else "Biotech Hubs & Genomic Core Facilities (gemini-3.8-flash)",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = BioEmeraldSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grounding Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BioDarkSurface,
                contentColor = BioCyanPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BioCyanPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Literature Grounding (Search)",
                            fontFamily = SpaceGrotesk,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Biotech Facilities (Maps)",
                            fontFamily = SpaceGrotesk,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Query Input Bar
            if (selectedTab == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("literature_search_input"),
                        placeholder = { Text("Search PubMed/bioRxiv papers...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BioCyanPrimary,
                            unfocusedBorderColor = BioDarkBorder
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.searchLiterature(searchQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = BioCyanPrimary, contentColor = BioDarkBackground),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("literature_search_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = locationCity,
                        onValueChange = { locationCity = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("biotech_location_input"),
                        placeholder = { Text("City or Biotech Hub (e.g. Cambridge, SF)...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BioCyanPrimary,
                            unfocusedBorderColor = BioDarkBorder
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.searchBiotechLocations(locationCity) },
                        colors = ButtonDefaults.buttonColors(containerColor = BioCyanPrimary, contentColor = BioDarkBackground),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("biotech_location_search_button")
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Locate")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    when (val state = searchResults) {
                        is UiState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = BioCyanPrimary)
                                }
                            }
                        }
                        is UiState.Error -> {
                            item {
                                Text("Search error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        is UiState.Success -> {
                            items(state.data) { item ->
                                LiteratureCard(item = item, onOpenUrl = { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                })
                            }
                        }
                    }
                } else {
                    when (val state = locationResults) {
                        is UiState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = BioCyanPrimary)
                                }
                            }
                        }
                        is UiState.Error -> {
                            item {
                                Text("Location error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        is UiState.Success -> {
                            items(state.data) { item ->
                                LocationCard(item = item, onOpenMaps = {
                                    val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(item.name + " " + item.addressOrCity)}"))
                                    context.startActivity(mapsIntent)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiteratureCard(
    item: LiteratureItem,
    onOpenUrl: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    text = item.sourceOrJournal.uppercase(),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = BioCyanPrimary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onOpenUrl(item.doiOrUrl) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "Open paper", tint = Color(0xFF90A4AE), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.title,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.abstractText,
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = Color(0xFFB0BEC5),
                lineHeight = 18.sp
            )
            if (item.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item.tags.forEach { tag ->
                        Text("#$tag", fontFamily = JetBrainsMono, fontSize = 10.sp, color = BioEmeraldSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationCard(
    item: BiotechLocationItem,
    onOpenMaps: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                        .background(BioEmeraldSecondary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.type.uppercase(),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BioEmeraldSecondary
                    )
                }
                IconButton(onClick = onOpenMaps, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "Open in Maps", tint = Color(0xFF90A4AE), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = item.addressOrCity,
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = Color(0xFF90A4AE)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.description,
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = Color(0xFFB0BEC5),
                lineHeight = 18.sp
            )
            if (item.specialties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item.specialties.forEach { spec ->
                        Text("• $spec", fontFamily = JetBrainsMono, fontSize = 10.sp, color = BioCyanPrimary)
                    }
                }
            }
        }
    }
}
