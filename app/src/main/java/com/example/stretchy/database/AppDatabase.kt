package com.example.stretchy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stretchy.database.converter.ActivityTypeConverter
import com.example.stretchy.database.converter.DateConverter
import com.example.stretchy.database.converter.TrainingTypeConverter
import com.example.stretchy.database.dao.*
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity
import com.example.stretchy.database.entity.metatraining.MetaTrainingEntity
import com.example.stretchy.database.entity.metatraining.MetaTrainingWithTrainingsCrossRef

@Database(
    entities = [TrainingEntity::class, ActivityEntity::class, MetaTrainingEntity::class, TrainingActivityEntity::class, MetaTrainingWithTrainingsCrossRef::class],
    version = 3
)
@TypeConverters(TrainingTypeConverter::class, ActivityTypeConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun trainingDao(): TrainingDao
    abstract fun metaTrainingDao(): MetaTrainingDao
    abstract fun metaTrainingWithTrainingsDao(): MetaTrainingWithTrainingsDao
    abstract fun trainingWithActivitiesDao(): TrainingWithActivitiesDao

    companion object {
        val MIGRATION_1_3: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `meta_training` " +
                            "(`metaTrainingId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`lastExecuted` TEXT)"
                )

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `meta_training_trainings_cross_ref` " +
                            "(`metaTrainingId` INTEGER NOT NULL, " +
                            "`trainingId` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`metaTrainingId`, `trainingId`))"
                )
            }
        }

        const val NAME = "stretchydb"
    }
}