package com.example.njttracker.common.analytics.firebase

import com.example.njttracker.common.analytics.AnalyticsLogger
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.recordException
import javax.inject.Inject

class FirebaseAnalyticsLogger(private val crashlytics: FirebaseCrashlytics) : AnalyticsLogger {

    @Inject
    constructor() : this(Firebase.crashlytics)

    override fun logError(message: String, throwable: Throwable) {
        crashlytics.log(message)
        crashlytics.recordException(throwable) {
            key("message", message)
        }
    }
}