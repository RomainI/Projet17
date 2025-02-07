package com.openclassrooms.rebonnte.ui.manageaccount

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openclassrooms.rebonnte.viewmodel.ManageAccountViewModel

@Composable
fun ManageAccountScreen(viewModel: ManageAccountViewModel = hiltViewModel()) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val deleteAccountResult by viewModel.deleteAccountState.collectAsState()

    if (isAuthenticated) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenue, utilisateur authentifié",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { viewModel.signOut() }) {
                Text("Se déconnecter")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { viewModel.deleteUserAccount() }) {
                Text("Supprimer le compte")
            }

            deleteAccountResult?.let { result ->
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (result) "Compte supprimé avec succès" else "Échec de la suppression",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vous n'êtes pas authentifié.",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}