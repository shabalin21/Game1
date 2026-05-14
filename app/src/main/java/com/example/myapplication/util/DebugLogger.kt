package com.example.myapplication.util

import timber.log.Timber

/**
 * Centralized logging for the production stabilization pass.
 */
object DebugLogger {
    
    fun logEvent(event: String) {
        Timber.tag("GameplayEvent").d(event)
    }
    
    fun logStatUpdate(stat: String, value: Any) {
        Timber.tag("Statistics").i("Stat Updated: $stat = $value")
    }
    
    fun logGameLoop(tickInfo: String) {
        Timber.tag("GameLoop").v(tickInfo)
    }
    
    fun logDatabaseWrite(table: String, info: String) {
        Timber.tag("Database").d("Write to $table: $info")
    }
    
    fun logError(message: String, throwable: Throwable? = null) {
        Timber.tag("StabilizationError").e(throwable, message)
    }
}
