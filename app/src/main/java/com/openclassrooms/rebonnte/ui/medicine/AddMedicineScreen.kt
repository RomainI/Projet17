package com.openclassrooms.rebonnte.ui.medicine

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: MedicineViewModel,
    aisleViewModel: AisleViewModel,
    onMedicineAdded: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedAisle by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("0") }
    var error by remember { mutableStateOf("") }
    val aisles by aisleViewModel.aisles.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(4.dp),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.add_medicine)) },
                navigationIcon = {
                    IconButton(onClick = {

                        activity.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = Color.Black
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
                label = { Text(stringResource(R.string.medicine_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedAisle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.aisle_choose)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
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
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = stock,
                onValueChange = {
                    stock = it.filter { char -> char.isDigit() }
                },
                label = { Text(stringResource(R.string.initial_stock)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (error.isNotEmpty()) {
                Text(text = error, color = Color.Red)
            }
            val addMedicineString = stringResource(R.string.all_field_mandatory)

            Button(
                onClick = {
                    if (name.isBlank() || selectedAisle.isBlank() || stock.isBlank()) {
                        error = addMedicineString
                    } else {
                        viewModel.addMedicine(
                            Medicine(
                                name = name,
                                nameAisle = selectedAisle,
                                stock = stock.toInt(),
                                histories = emptyList()
                            )
                        )
                        onMedicineAdded()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.add_medicine))
            }
        }
    }
}