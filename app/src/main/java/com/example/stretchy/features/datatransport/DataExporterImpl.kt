package com.example.stretchy.features.datatransport

import android.os.Environment
import com.example.stretchy.repository.Repository
import com.google.gson.GsonBuilder
import java.io.File

class DataExporterImpl(val repository: Repository) : DataExporter {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val dataTransportFilePath: String =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

    private val dataTransportFileName: String = "StretchyTrainings$dataTransportFileExt"
    private val dataFile = File(dataTransportFilePath, dataTransportFileName)

    override suspend fun export() {
        val data = getSavedData()
        dataFile.writeText(data!!)
    }

    private suspend fun getSavedData(): String? {
        return gson.toJson(repository.getTrainingsWithActivities())
    }

    companion object {
        const val dataTransportFileExt: String = ".rafalczamp"
    }
}