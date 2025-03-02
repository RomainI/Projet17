package com.openclassrooms.rebonnte.ui.aisle

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel

/**
 * Composable screen for displaying the list of aisles from AisleViewModel
 * Uses LazyColumn
 */

@Composable
fun AisleScreen(viewModel: AisleViewModel, isDarkMode: Boolean) {
    val aisles by viewModel.aisles.collectAsState(initial = emptyList())
    val context = LocalContext.current
    if (aisles.isEmpty()){
        Text (stringResource(R.string.empty_list_aisle))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(aisles) { aisle ->
                AisleItem(aisle = aisle, onClick = {
                    startDetailActivity(context, aisle.name, isDarkMode = isDarkMode )
                })
            }
        }
    }
}

@Composable
fun AisleItem(aisle: Aisle, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = aisle.name, style = MaterialTheme.typography.bodyMedium)
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = stringResource(R.string.arrow_icon))
    }
}

private fun startDetailActivity(context: Context, name: String , isDarkMode : Boolean) {
    val intent = Intent(context, AisleDetailActivity::class.java).apply {
        putExtra("nameAisle", name)
        putExtra("isDarkMode", isDarkMode)
    }
    context.startActivity(intent)
}