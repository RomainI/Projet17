package com.openclassrooms.rebonnte.ui.medicine

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.openclassrooms.rebonnte.R
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.utils.AuthUtils.startFirebaseUIAuth
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Activity and composable screen for displaying data about an unique medicine
 * Supports image uploads, stock displaying and increment/decrement, and modification history inside a LazyRom
 */

@AndroidEntryPoint
class MedicineDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("nameMedicine") ?: "Unknown"
        val isDarkMode = intent.getBooleanExtra("isDarkMode", false)

        val viewModel: MedicineViewModel by viewModels()
        val aisleViewModel: AisleViewModel by viewModels()


        setContent {
            RebonnteTheme (isDarkMode) {
                MedicineDetailScreen(name, viewModel, aisleViewModel, isDarkMode)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    name: String, viewModel: MedicineViewModel, aisleViewModel: AisleViewModel, isDarkMode : Boolean
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val medicine = medicines.find { it.name == name } ?: return
    val context = LocalContext.current
    val activity = context as Activity
    val aisles by aisleViewModel.aisles.collectAsState(initial = emptyList())
    val aisle = aisles.find { it.name == medicine.nameAisle }
    var imageMedicineUrl by remember { mutableStateOf(medicine?.photoUrl) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val tint = if (isDarkMode) Color.White else Color.Black



    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                if (aisle != null) {
                    viewModel.updateMedicineImage(it, medicine) { uploadedUrl ->
                        imageMedicineUrl = uploadedUrl
                    }
                }
            }
        }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${medicine.name} Detail") },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = tint
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {


            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        val medicinePhoto =
                            if (medicine.photoUrl == null) {
                                if (imageUri == null) {
                                    if(isDarkMode) R.drawable.add_image_invert else R.drawable.add_image
                                } else imageUri
                            } else medicine.photoUrl

                        AsyncImage(
                            model = medicinePhoto,
                            contentDescription = stringResource(R.string.medicine_image),
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { launcher.launch("image/*") },
                            contentScale = ContentScale.Crop,
                        )
                        val text =
                            if (medicine.photoUrl == null && imageUri == null) stringResource(R.string.click_here_medicine) else medicine.name + " photo"
                        Text(
                            text = text,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        AsyncImage(
                            model = aisle?.mapUrl,
                            contentDescription = stringResource(R.string.aisle_map),
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(4.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = stringResource(R.string.floor_map),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        val color = if (medicine.stock < 10) Color.Red else tint
                        Text(
                            text = stringResource(R.string.stock_left) + " " + medicine.stock,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.increase),
                            modifier = Modifier.clickable {
                                viewModel.incrementStock(
                                    medicine,
                                    FirebaseAuth.getInstance().currentUser?.email
                                )
                            },
                            tint= tint
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.decrease),
                            modifier = Modifier.clickable {
                                viewModel.decrementStock(
                                    medicine,
                                    FirebaseAuth.getInstance().currentUser?.email
                                )
                            },
                            tint= tint
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Divider(color = Color.Black, thickness = 2.dp)

                Text(
                    text = stringResource(R.string.modification_history),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRow {
                        items(medicine.histories) { history ->
                            HistoryItem(history = history)
                        }
                    }
                }

                val lastModified = medicine.histories.lastOrNull()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.last_modification_by) + "\n ${lastModified?.userEmail ?: "Unknown"} on ${
                            formatDate(
                                lastModified?.date
                            )
                        }",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Card(
        modifier = Modifier
            .width(screenWidth)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = history.details)
            Text(text = "by ${history.userEmail}")
            Text(text = "on ${formatDate(history.date)}")
        }
    }
}

fun formatDate(date: Date?): String {
    return if (date != null) {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        formatter.format(date)
    } else {
        "NaN"
    }
}
