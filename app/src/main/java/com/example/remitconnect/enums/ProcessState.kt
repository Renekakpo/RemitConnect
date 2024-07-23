package com.example.remitconnect.enums

sealed class ProcessState {
    object Loading : ProcessState()
    object Done : ProcessState()
    data class Error(val message: String) : ProcessState()
}