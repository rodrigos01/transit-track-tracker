package com.example.njttracker.common.analytics

interface AnalyticsLogger {
    fun logError(message: String, throwable: Throwable)
}