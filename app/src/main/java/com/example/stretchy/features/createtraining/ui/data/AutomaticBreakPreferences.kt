package com.example.stretchy.features.createtraining.ui.data

import android.content.Context

class AutomaticBreakPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(AUTOMATIC_BREAK_DURATION, Context.MODE_PRIVATE)

    fun updateAutoBreakDuration(duration: Int) {
        val editor = prefs.edit()
        editor.putInt(AUTOMATIC_BREAK_DURATION, duration)
        editor.apply()
    }

    fun getCurrentAutoBreakDuration() = prefs.getInt(AUTOMATIC_BREAK_DURATION, 5)

    companion object {
        const val AUTOMATIC_BREAK_DURATION = "breakDuration"
    }
}