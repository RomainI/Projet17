package com.openclassrooms.rebonnte.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: MedicineViewModel,
    aisleViewModel: AisleViewModel,
    onMedicineAdded: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedAisle by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("0") }
    var error by remember { mutableStateOf("") }
    val aisles by aisleViewModel.aisles.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(16.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom du médicament") },
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
                    label = { Text("Choisir une allée") },
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
                label = { Text("Quantité initiale en stock") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (error.isNotEmpty()) {
                Text(text = error, color = androidx.compose.ui.graphics.Color.Red)
            }

            Button(
                onClick = {
                    if (name.isBlank() || selectedAisle.isBlank() || stock.isBlank()) {
                        error = "Tous les champs sont obligatoires"
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
                Text(text = "Ajouter le médicament")
            }
        }
    }
}