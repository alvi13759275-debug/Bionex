package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.ui.MainViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOut
import com.example.ui.screens.ChatbotScreen
import com.example.ui.screens.GenomicsLabScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiteratureSearchScreen
import com.example.ui.screens.ProtocolScreen
import com.example.ui.screens.VoiceLiveScreen
import com.example.ui.theme.BioCyanPrimary
import com.example.ui.theme.BioDarkBackground
import com.example.ui.theme.BioDarkBorder
import com.example.ui.theme.BioDarkSurface
import com.example.ui.theme.BioEmeraldSecondary
import com.example.ui.theme.BioNexusTheme
import com.example.ui.theme.BioPurpleTertiary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // MANDATORY: Configure App Check with launch Intent extra before rendering UI or Firebase operations
        configureAppCheck(this, intent)

        setContent {
            BioNexusTheme {
                BioNexusAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BioNexusAppRoot(viewModel: MainViewModel) {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        MainAuthenticatedApp(viewModel = viewModel)
    }
}

sealed class NavDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Home : NavDestination("home", "Overview", Icons.Default.Home)
    data object Genomics : NavDestination("genomics", "Genomics", Icons.Default.Biotech)
    data object Protocols : NavDestination("protocols", "Protocols", Icons.Default.Science)
    data object Chatbot : NavDestination("chatbot", "AI Chat", Icons.Default.Chat)
    data object Search : NavDestination("search", "Grounding", Icons.Default.Book)
    data object Voice : NavDestination("voice", "Live Audio", Icons.Default.Mic)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAuthenticatedApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val user = Firebase.auth.currentUser

    BackHandler(enabled = currentScreen != "home") {
        viewModel.navigateTo("home")
    }

    val destinations = listOf(
        NavDestination.Home,
        NavDestination.Genomics,
        NavDestination.Protocols,
        NavDestination.Chatbot,
        NavDestination.Search,
        NavDestination.Voice
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(BioCyanPrimary.copy(alpha = 0.2f))
                                .border(1.dp, BioCyanPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Biotech,
                                contentDescription = null,
                                tint = BioCyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "BioNexus",
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = user?.email ?: "Verified Researcher",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = BioEmeraldSecondary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            signOut(context, credentialManager, onSignOutComplete = {}, scope = coroutineScope)
                        },
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Color(0xFF90A4AE)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BioDarkSurface,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BioDarkSurface,
                tonalElevation = 8.dp
            ) {
                destinations.forEach { dest ->
                    val isSelected = currentScreen == dest.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(dest.route) },
                        icon = {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = dest.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = dest.label,
                                fontFamily = SpaceGrotesk,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BioDarkBackground,
                            selectedTextColor = BioCyanPrimary,
                            indicatorColor = BioCyanPrimary,
                            unselectedIconColor = Color(0xFF78909C),
                            unselectedTextColor = Color(0xFF78909C)
                        ),
                        modifier = Modifier.testTag("nav_item_${dest.route}")
                    )
                }
            }
        },
        containerColor = BioDarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(viewModel = viewModel)
                "genomics" -> GenomicsLabScreen(viewModel = viewModel)
                "protocols" -> ProtocolScreen(viewModel = viewModel)
                "chatbot" -> ChatbotScreen(viewModel = viewModel)
                "search" -> LiteratureSearchScreen(viewModel = viewModel)
                "voice" -> VoiceLiveScreen(viewModel = viewModel)
                else -> HomeScreen(viewModel = viewModel)
            }
        }
    }
}
