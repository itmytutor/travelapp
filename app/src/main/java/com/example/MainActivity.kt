package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Destination
import com.example.ui.components.CelebrationDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddEditDestinationScreen
import com.example.ui.screens.DestinationDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.TravelListScreen
import com.example.ui.theme.WanderListTheme
import com.example.viewmodel.DestinationEvent
import com.example.viewmodel.DestinationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: DestinationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanderListTheme {
                WanderListApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WanderListApp(viewModel: DestinationViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val allDestinations by viewModel.allDestinations.collectAsStateWithLifecycle()
    val filteredDestinations by viewModel.filteredDestinations.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.filterStatus.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentSort by viewModel.sortBy.collectAsStateWithLifecycle()

    var celebrationDestination by remember { mutableStateOf<Destination?>(null) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DestinationEvent.DestinationVisited -> {
                    celebrationDestination = event.destination
                }
                is DestinationEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    celebrationDestination?.let { dest ->
        CelebrationDialog(
            destination = dest,
            onDismiss = { celebrationDestination = null },
            onSaveRating = { rating ->
                viewModel.updateRating(dest, rating)
            }
        )
    }

    val isTopLevelScreen = currentScreen is Screen.Home ||
            currentScreen is Screen.TravelList ||
            currentScreen is Screen.ProfileSettings

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Home,
                        onClick = { currentScreen = Screen.Home },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.TravelList,
                        onClick = { currentScreen = Screen.TravelList },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "My Travel List"
                            )
                        },
                        label = { Text("My Travel List") },
                        modifier = Modifier.testTag("nav_travel_list")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.ProfileSettings,
                        onClick = { currentScreen = Screen.ProfileSettings },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile & Settings"
                            )
                        },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        stats = stats,
                        destinations = allDestinations,
                        onNavigateToTravelList = { filter ->
                            if (filter != null) {
                                viewModel.setFilterStatus(filter)
                            }
                            currentScreen = Screen.TravelList
                        },
                        onDestinationClick = { id ->
                            currentScreen = Screen.Detail(id)
                        },
                        onAddNewClick = {
                            currentScreen = Screen.AddEdit(null)
                        },
                        onRestoreSamples = {
                            viewModel.restoreSamples()
                        }
                    )
                }

                is Screen.TravelList -> {
                    TravelListScreen(
                        destinations = filteredDestinations,
                        stats = stats,
                        selectedStatus = selectedStatus,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        currentSort = currentSort,
                        onStatusSelected = { viewModel.setFilterStatus(it) },
                        onCategorySelected = { viewModel.setFilterCategory(it) },
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onSortChanged = { viewModel.setSortBy(it) },
                        onDestinationClick = { id ->
                            currentScreen = Screen.Detail(id)
                        },
                        onToggleVisited = { destination ->
                            viewModel.toggleVisited(destination)
                        },
                        onAddNewClick = {
                            currentScreen = Screen.AddEdit(null)
                        },
                        onRestoreSamples = {
                            viewModel.restoreSamples()
                        }
                    )
                }

                is Screen.ProfileSettings -> {
                    ProfileSettingsScreen(
                        stats = stats,
                        onShowMessage = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }

                is Screen.Detail -> {
                    val destination = allDestinations.find { it.id == screen.destinationId }
                    if (destination != null) {
                        DestinationDetailScreen(
                            destination = destination,
                            onBackClick = { currentScreen = Screen.TravelList },
                            onEditClick = { currentScreen = Screen.AddEdit(destination.id) },
                            onDeleteClick = {
                                viewModel.deleteDestination(it)
                                currentScreen = Screen.TravelList
                            },
                            onStatusChange = { newStatus ->
                                viewModel.saveDestination(destination.copy(status = newStatus))
                            },
                            onToggleActivity = { activityId ->
                                viewModel.toggleActivity(destination, activityId)
                            },
                            onAddActivity = { text ->
                                viewModel.addActivity(destination, text)
                            },
                            onDeleteActivity = { activityId ->
                                viewModel.deleteActivity(destination, activityId)
                            },
                            onRate = { rating ->
                                viewModel.updateRating(destination, rating)
                            }
                        )
                    } else {
                        // Destination might have been deleted
                        LaunchedEffect(Unit) {
                            currentScreen = Screen.TravelList
                        }
                    }
                }

                is Screen.AddEdit -> {
                    val destinationToEdit = screen.destinationId?.let { id ->
                        allDestinations.find { it.id == id }
                    }
                    AddEditDestinationScreen(
                        destinationToEdit = destinationToEdit,
                        onBackClick = {
                            currentScreen = if (screen.destinationId != null) {
                                Screen.Detail(screen.destinationId)
                            } else {
                                Screen.TravelList
                            }
                        },
                        onSave = { savedDest ->
                            viewModel.saveDestination(savedDest)
                            currentScreen = Screen.TravelList
                        }
                    )
                }
            }
        }
    }
}
