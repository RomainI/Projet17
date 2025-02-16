//import android.provider.Settings.Global.getString
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.openclassrooms.rebonnte.R
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddAisleDialog(
//    onDismiss: () -> Unit,
//    onAddAisle: (String) -> Unit
//) {
//    var aisleName by remember { mutableStateOf("") }
//
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        title = { Text(getString(R.string.add_new_aisle)) },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = aisleName,
//                    onValueChange = { aisleName = it },
//                    label = { Text("Aisle Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    if (aisleName.isNotBlank()) {
//                        onAddAisle(aisleName)
//                    }
//                }
//            ) {
//                Text("Add")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    )
//}