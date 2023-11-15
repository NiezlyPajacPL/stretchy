package com.example.stretchy.features.datatransport

import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataImporterImpl(val repository: Repository) : DataImporter {
    private val gson = Gson()
    override suspend fun importByAppending(data: String) {
        getData(data).forEach { training ->
            repository.addTrainingWithActivities(training)
        }
    }

    override suspend fun importByOverriding(data: String) {
        repository.deleteAllTrainings()
        getData(data).forEach { training ->
            repository.addTrainingWithActivities(training)
        }
    }

    private fun getData(data: String): List<TrainingWithActivity> {
        val trainingsWithActivity = object : TypeToken<List<TrainingWithActivity>>() {}.type
        return gson.fromJson(data, trainingsWithActivity)
    }
}