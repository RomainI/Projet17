package com.openclassrooms.rebonnte.ui.medicine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.MainActivity
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.utils.AuthUtils.startFirebaseUIAuth
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class MedicineDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("nameMedicine") ?: "Unknown"
        val viewModel: MedicineViewModel by viewModels()

        setContent {
            RebonnteTheme {
                MedicineDetailScreen(name, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    name: String, viewModel: MedicineViewModel
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val medicine = medicines.find { it.name == name } ?: return
    val stock = medicine.stock
    val context = LocalContext.current
    val activity = context as Activity
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Medicine detail")
                },
                navigationIcon = {
                    IconButton(onClick = {
//                        val intent = Intent(activity, MainActivity::class.java)
//                        context.startActivity(intent)
                        activity?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "go back",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    )

    { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = medicine.name,
                onValueChange = {},
                label = { Text("Name") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = medicine.nameAisle,
                onValueChange = {},
                label = { Text("Aisle") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val activity = LocalContext.current as? Activity
                IconButton(onClick = {
                    if (currentUser != null) {
//                        if (stock > 0) {
//                            medicines[medicines.size].histories.toMutableList().add(
//                                History(
//                                    medicine.name,
//                                    currentUser.email,
//                                    Date(),
//                                    "Updated medicine details"
//                                )
//                            )
//                            stock--
//                        }

                        viewModel.decrementStock(medicine, currentUser.email)
                    } else {
                        if (activity != null) {
                            startFirebaseUIAuth(activity)
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minus One"
                    )
                }
                TextField(
                    value = stock.toString(),
                    onValueChange = {},
                    label = { Text("Stock") },
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (currentUser != null) {
//                        medicines[medicines.size].histories.toMutableList().add(
//                            History(
//                                medicine.name,
//                                currentUser.email,
//                                Date(),
//                                "Updated medicine details"
//                            )
//                        )
//                        stock++
                        viewModel.incrementStock(medicine, currentUser.email)

                    } else {
                        if (activity != null) {
                            startFirebaseUIAuth(activity)
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Plus One"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(medicine.histories) { history ->
                    HistoryItem(history = history)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = history.medicineName, fontWeight = FontWeight.Bold)
            Text(text = "User: ${history.userEmail}")
            Text(text = "Date: ${history.date}")
            Text(text = "Details: ${history.details}")
        }
    }
}