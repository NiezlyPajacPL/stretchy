package com.example.stretchy.common

fun convertSecondsToMinutes(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours < 1) "$minutes m ${seconds % 60}s" else {
        "$hours h ${minutes % 60}m"
    }
}