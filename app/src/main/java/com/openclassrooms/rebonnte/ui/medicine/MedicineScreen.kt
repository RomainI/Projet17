package com.openclassrooms.rebonnte.ui.medicine

import android.content.Context
import androidx.compose.runtime.Composable

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel

/**
 * Composable screen for displaying the list of medicines from MedicineViewModel
 * Uses LazyColumn and swipe-to-delete
 */

@Composable
fun MedicineScreen(viewModel: MedicineViewModel = viewModel(), isDarkMode: Boolean) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val context = LocalContext.current
    if (medicines.isEmpty()) {
        Text(stringResource(R.string.empty_list))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(medicines) { medicine ->
                MedicineItem(medicine = medicine, onClick = {
                    startDetailActivity(context, medicine.name, isDarkMode)
                }, medicineViewModel = viewModel, isDarkMode)
            }
        }
    }
}

@Composable
fun MedicineItem(
    medicine: Medicine,
    onClick: () -> Unit,
    medicineViewModel: MedicineViewModel,
    isDarkMode: Boolean
) {
    SwipeToDeleteItem(
        onDelete = { medicineViewModel.deleteMedicine(medicine.id) },
        resetState = false,
        content = { modifier ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                val model = if(! isDarkMode) medicine.photoUrl ?: R.drawable.add_image else medicine.photoUrl ?: R.drawable.add_image_invert
                AsyncImage(
                    model = model,
                    contentDescription = stringResource(R.string.medicine_image),
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = medicine.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Stock: ${medicine.stock}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.arrow_icon)
                )
            }
        }
    )
}

private fun startDetailActivity(context: Context, name: String, isDarkMode: Boolean) {
    val intent = Intent(context, MedicineDetailActivity::class.java).apply {
        putExtra("nameMedicine", name)
        putExtra("isDarkMode", isDarkMode)
    }
    context.startActivity(intent)
}
