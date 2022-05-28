package com.pureblissy.android

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    init {
        viewModelScope.launch {
            delay(500)
            _isLoading.value = false
        }
    }
    fun mockDataLoading(): Boolean {
        viewModelScope.launch {
            delay(5000)
            _isLoading.value = true
        }
        return _isLoading.value
    }
}