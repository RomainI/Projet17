package com.openclassrooms.rebonnte.ui.medicine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.viewmodel.AisleViewModel
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Activity for adding a new medicine launching AddMedicineScreen
 */

@AndroidEntryPoint
class AddMedicineActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val medicineViewModel: MedicineViewModel by viewModels()
        val aisleViewModel: AisleViewModel by viewModels()
        val isDarkMode = intent.getBooleanExtra("ISDARKMODE", false)


        setContent {
            RebonnteTheme (darkTheme = isDarkMode){
                AddMedicineScreen(
                    viewModel = medicineViewModel,
                    aisleViewModel = aisleViewModel,
                    isDarkMode = isDarkMode,
                    onMedicineAdded = { onBackPressedDispatcher.onBackPressed() }

                )
            }
        }
    }
}