package com.openclassrooms.rebonnte.ui.manageaccount

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openclassrooms.rebonnte.R
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
                text = stringResource(R.string.welcome)+" "+viewModel.getAccountName(),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { viewModel.signOut() }) {
                Text(stringResource(R.string.log_out))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { viewModel.deleteUserAccount() }) {
                Text(stringResource(R.string.delete_account))
            }

            deleteAccountResult?.let { result ->
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (result) stringResource(R.string.delete_success) else stringResource(R.string.delete_failed),
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
                text = stringResource(R.string.not_auth),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}