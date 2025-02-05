package com.openclassrooms.rebonnte

import android.app.Activity
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.medicine.AddMedicineActivity
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.utils.AuthUtils.startFirebaseUIAuth
import com.openclassrooms.rebonnte.utils.BroadcastReceiverManager
import com.openclassrooms.rebonnte.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var broadcastReceiverManager: BroadcastReceiverManager

    val mainViewModel: MainViewModel by viewModels()
    val medicineViewModel: MedicineViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MyApp(mainViewModel)
        }
        broadcastReceiverManager.setOnBroadcastReceivedListener {
            medicineViewModel.refreshMedicines()
        }

        mainViewModel.startBroadcastReceiver()
    }

    override fun onResume() {
        super.onResume()
        medicineViewModel.loadMedicines()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val medicineViewModel: MedicineViewModel = viewModel()
    val aisleViewModel: AisleViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route

    RebonnteTheme {
        Scaffold(
            topBar = {
                var isSearchActive by rememberSaveable { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }

                Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                    TopAppBar(
                        title = { if (route == "aisle") Text(text = "Aisle") else Text(text = "Medicines") },
                        actions = {
                            var expanded by remember { mutableStateOf(false) }
                            if (currentRoute(navController) == "medicine") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Box {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = null)
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            offset = DpOffset(x = 0.dp, y = 0.dp)
                                        ) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    medicineViewModel.sortByNone()
                                                    expanded = false
                                                },
                                                text = { Text("Sort by None") }
                                            )
                                            DropdownMenuItem(
                                                onClick = {
                                                    medicineViewModel.sortByName()
                                                    expanded = false
                                                },
                                                text = { Text("Sort by Name") }
                                            )
                                            DropdownMenuItem(
                                                onClick = {
                                                    medicineViewModel.sortByStock()
                                                    expanded = false
                                                },
                                                text = { Text("Sort by Stock") }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                    if (currentRoute(navController) == "medicine") {
                        EmbeddedSearchBar(
                            query = searchQuery,
                            onQueryChange = {
                                medicineViewModel.filterByName(it)
                                searchQuery = it
                            },
                            isSearchActive = isSearchActive,
                            onActiveChanged = { isSearchActive = it }
                        )
                    }
                }

            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Aisle") },
                        selected = currentRoute(navController) == "aisle",
                        onClick = { navController.navigate("aisle") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("Medicine") },
                        selected = currentRoute(navController) == "medicine",
                        onClick = { navController.navigate("medicine") }
                    )
                }
            },
            floatingActionButton = {
//                FloatingActionButton(onClick = {
//                    val activity = LocalContext.current as? Activity
//                    val currentUser = FirebaseAuth.getInstance().currentUser
//                    if (currentUser != null) {
//                        if (route == "medicine") {
//
//
//                            //TODO medicineViewModel.addRandomMedicine(aisleViewModel.aisles.value)
//                        } else if (route == "aisle") {
//
//
//                            //TODO aisleViewModel.addRandomAisle()
//                        }
//                    } else {
//                            //if (route == "medicine") {
//
//                                //TODO medicineViewModel.addRandomMedicine(aisleViewModel.aisles.value)
//                            //} else if (route == "aisle") {
//                        if (activity != null) {
//                            startFirebaseUIAuth(activity)
//                        }
//
//                                //TODO aisleViewModel.addRandomAisle()
//                            //}
//
//                    }
//
//
//                }) {
//                    Icon(Icons.Default.Add, contentDescription = "Add")
//                }
                FloatingActionButtonWithAuth(
                    route = route ?: "",
                    medicineViewModel = medicineViewModel,
                    aisleViewModel = aisleViewModel
                )

            }
        ) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = "aisle"
            ) {
                composable("aisle") { AisleScreen(aisleViewModel) }
                composable("medicine") { MedicineScreen(medicineViewModel) }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun EmbeddedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf(query) }
    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }

    val shape: Shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchActive) {
            IconButton(onClick = { activeChanged(false) }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        BasicTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                onQueryChange(query)
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            }
        )

        if (isSearchActive && searchQuery.isNotEmpty()) {
            IconButton(onClick = {
                searchQuery = ""
                onQueryChange("")
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
@Composable
fun FloatingActionButtonWithAuth(
    route: String,
    medicineViewModel: MedicineViewModel,
    aisleViewModel: AisleViewModel
) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val currentUser = FirebaseAuth.getInstance().currentUser

    var showAddAisleDialog by remember { mutableStateOf(false) }

    if (showAddAisleDialog) {
        AddAisleDialog(
            onDismiss = { showAddAisleDialog = false },
            onAddAisle = { aisleName ->
                aisleViewModel.addAisle(aisleName)
                showAddAisleDialog = false
            }
        )
    }

    FloatingActionButton(onClick = {
        if (currentUser != null) {
            if (route == "medicine") {
                val intent = Intent(activity, AddMedicineActivity::class.java)
                context.startActivity(intent)
            } else if (route == "aisle") {
                showAddAisleDialog = true
            }
        } else {
            if (activity != null) {
                startFirebaseUIAuth(activity)
            } else {
                Log.d("MainActivity", "Impossible de démarrer l'authentification")
            }
        }
    }) {
        Icon(Icons.Default.Add, contentDescription = "Add")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAisleDialog(
    onDismiss: () -> Unit,
    onAddAisle: (String) -> Unit
) {
    var aisleName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (aisleName.isNotBlank()) {
                    onAddAisle(aisleName.trim())
                }
            }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = { Text("Ajouter une nouvelle allée") },
        text = {
            TextField(
                value = aisleName,
                onValueChange = { aisleName = it },
                label = { Text("Nom de l'allée") }
            )
        }
    )
}