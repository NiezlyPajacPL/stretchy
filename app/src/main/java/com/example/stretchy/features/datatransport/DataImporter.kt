package com.example.stretchy.features.datatransport

interface DataImporter {
    suspend fun importByAppending(data: String)

    suspend fun importByOverriding(data:String)
}