package com.example.myapplication.domain.terminal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalLogManager @Inject constructor() {
    private val _logs = MutableStateFlow<List<TerminalLog>>(emptyList())
    val logs: StateFlow<List<TerminalLog>> = _logs.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun log(category: LogCategory, message: String) {
        val timestamp = timeFormat.format(Date())
        val logEntry = TerminalLog(timestamp, category, message)
        _logs.value = (_logs.value + logEntry).takeLast(100)
    }
}

data class TerminalLog(
    val timestamp: String,
    val category: LogCategory,
    val message: String
)

enum class LogCategory {
    ECONOMY, CASINO, NEURAL, EMOTION, WARNING, CRITICAL, SYSTEM, MARKET
}
