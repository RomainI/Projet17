package com.openclassrooms.rebonnte.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.openclassrooms.rebonnte.utils.BroadcastReceiverManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {

}