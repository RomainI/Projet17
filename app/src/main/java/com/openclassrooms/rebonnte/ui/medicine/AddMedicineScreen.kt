package com.openclassrooms.rebonnte.ui.medicine

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel

/**
 * Composable screen for adding a new medicine.
 * Supports image uploads, barcode scanning with CameraCaptureScreen, and aisle selection from existing aisles
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: MedicineViewModel,
    aisleViewModel: AisleViewModel,
    onMedicineAdded: () -> Unit,
    isDarkMode: Boolean
) {
    var name by remember { mutableStateOf("") }
    var selectedAisle by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("0") }
    var error by remember { mutableStateOf("") }
    val aisles by aisleViewModel.aisles.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity
    var expanded by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            isUploading = true
            viewModel.uploadImage(imageUri!!) { returnedUrl ->
                imageUrl = returnedUrl
                isUploading = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(4.dp),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.add_medicine)) },
                navigationIcon = {
                    IconButton(onClick = {

                        activity.finish()
                    }) {
                        val tint = if (isDarkMode) Color.White else Color.Black
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .fillMaxWidth()
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.medicine_name), fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedAisle,
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    stringResource(R.string.aisle_choose),
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            aisles.forEach { aisle ->
                                DropdownMenuItem(
                                    text = { Text(aisle.name) },
                                    onClick = {
                                        selectedAisle = aisle.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                TextField(
                    value = stock,
                    onValueChange = {
                        stock = it.filter { char -> char.isDigit() }
                    },
                    label = { Text(stringResource(R.string.initial_stock)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            val model = if (isDarkMode) imageUri ?: R.drawable.add_image_invert else imageUri
                ?: R.drawable.add_image
            AsyncImage(
                model = model,
                contentDescription = stringResource(R.string.medicine_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
                    .clickable { launcher.launch("image/*") }
            )
            if (error.isNotEmpty()) {
                Text(text = error, color = Color.Red)
            }
            val addMedicineString = stringResource(R.string.all_field_mandatory)


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Color.Gray
                )
                Text(
                    text = stringResource(R.string.or),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Color.Gray
                )
            }
            var showCamera by remember { mutableStateOf(false) }

            if (showCamera) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(height = 300.dp, width = 150.dp)
                    ) {
                        CameraCaptureScreen(viewModel)
                    }
                }
            } else {
                val barcodeModel = if (isDarkMode) R.drawable.barcode_invert else R.drawable.barcode
                AsyncImage(
                    model = barcodeModel,
                    contentDescription = stringResource(R.string.barcode_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clickable { showCamera = true }
                )

            }
            val isButtonEnabled =
                name.isNotBlank() && selectedAisle.isNotBlank() && stock.isNotBlank() && !isUploading
            Button(
                onClick = {
                    if (!isButtonEnabled) {
                        error = addMedicineString
                    } else {
                        val medicine = Medicine(
                            name = name,
                            nameAisle = selectedAisle,
                            stock = stock.toInt(),
                            histories = emptyList(),
                            photoUrl = imageUrl
                        )
                        viewModel.addMedicine(medicine)
                        onMedicineAdded()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (name.isNotBlank() && selectedAisle.isNotBlank() && stock.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text(text = stringResource(R.string.add_medicine), fontSize = 14.sp)
            }
        }
    }
}