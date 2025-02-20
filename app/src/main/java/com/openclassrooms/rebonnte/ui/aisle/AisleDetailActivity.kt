package com.openclassrooms.rebonnte.ui.aisle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.openclassrooms.rebonnte.MainActivity
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailActivity
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity and composable screen for displaying data about an unique aisle
 * Supports image upload and list medicines attached to the aisle
 */

@AndroidEntryPoint
class AisleDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("nameAisle") ?: "Unknown"
        val viewModel: MedicineViewModel by viewModels()
        val aisleViewModel: AisleViewModel by viewModels()
        setContent {
            RebonnteTheme {
                AisleDetailScreen(name, viewModel, aisleViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(name: String, viewModel: MedicineViewModel, aisleViewModel: AisleViewModel) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val filteredMedicines = medicines.filter { it.nameAisle == name }
    val context = LocalContext.current
    val activity = context as Activity
    val aisles by aisleViewModel.aisles.collectAsState(initial = emptyList())
    val aisle = aisles.find { it.name == name }
    var imageMapUrl by remember {
        mutableStateOf(
            aisle?.mapUrl
                ?: "https://img.leboncoin.fr/api/v1/lbcpb1/images/38/44/27/384427e9153618a1f642af772923916ce6e96743.jpg?rule=ad-image"
        )
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                if (aisle != null) {
                    aisleViewModel.uploadImage(it, aisle) { uploadedUrl ->
                        imageMapUrl = uploadedUrl
                    }
                }
            }
        }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.aisle_detail))
            },
            navigationIcon = {
                IconButton(onClick = {
                    activity?.finish()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back),
                        tint = Color.Black
                    )
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween
            ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                items(filteredMedicines) { medicine ->
                    MedicineItem(medicine = medicine, onClick = { name ->
                        val intent = Intent(context, MedicineDetailActivity::class.java).apply {
                            putExtra("nameMedicine", name)
                        }
                        context.startActivity(intent)
                    })
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.click_floor_image),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )


                AsyncImage(

                    model = imageMapUrl,
                    contentDescription = stringResource(R.string.uploaded_image),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { launcher.launch("image/*") }
                        .size(300.dp),


                    )
            }
        }
    }
}

@Composable
fun MedicineItem(medicine: Medicine, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(medicine.name) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicine.name, fontWeight = FontWeight.Bold)
            Text(text = "Stock: ${medicine.stock}", color = Color.Gray)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = stringResource(R.string.arrow_icon))
    }
}
