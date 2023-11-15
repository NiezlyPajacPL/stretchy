package com.example.stretchy.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stretchy.database.converter.ActivityTypeConverter
import com.example.stretchy.database.converter.TrainingTypeConverter
import com.example.stretchy.database.dao.ActivityDao
import com.example.stretchy.database.dao.TrainingDao
import com.example.stretchy.database.dao.TrainingWithActivitiesDao
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity

@Database(
    entities = [TrainingEntity::class, ActivityEntity::class, TrainingActivityEntity::class],
    version = 2
)
@TypeConverters(TrainingTypeConverter::class, ActivityTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun trainingDao(): TrainingDao
    abstract fun trainingWithActivitiesDao(): TrainingWithActivitiesDao

    companion object {
        const val NAME = "stretchydb"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            @SuppressLint("Range")
            override fun migrate(database: SupportSQLiteDatabase) {
                fun addActivityOrderColumn() {
                    database.execSQL("ALTER TABLE training_activities ADD COLUMN activityOrder INTEGER NOT NULL DEFAULT 0")

                    database.execSQL(
                        "UPDATE training_activities " +
                                "SET activityOrder = (SELECT COUNT(*) " +
                                "                    FROM training_activities AS ta " +
                                "                    WHERE ta.tId = training_activities.tId " +
                                "                    AND ta.aId <= training_activities.aId)"
                    )
                }

                fun addBreakActivity() {
                    database.execSQL(
                        "INSERT INTO activity (name, duration, activityType) " +
                                "VALUES ('', 5, 'BREAK')"
                    )
                }

                fun changePrimaryKeys() {
                    database.execSQL(
                        "CREATE TABLE training_activities_new (" +
                                "tId INTEGER NOT NULL, " +
                                "aId INTEGER NOT NULL, " +
                                "activityOrder INTEGER NOT NULL, " +
                                "PRIMARY KEY (tId, activityOrder))"
                    )

                    database.execSQL(
                        "INSERT INTO training_activities_new (tId, aId, activityOrder) " +
                                "SELECT tId, aId, activityOrder FROM training_activities"
                    )

                    database.execSQL("DROP TABLE training_activities")

                    database.execSQL("ALTER TABLE training_activities_new RENAME TO training_activities")
                }

                fun getBreakId(): Long? {
                    val cursor =
                        database.query("SELECT activityId FROM activity WHERE activityType = 'BREAK' AND duration = 5")
                    val breakId: Long? = if (cursor.moveToFirst()) {
                        cursor.getLong(0)
                    } else {
                        null
                    }
                    return breakId
                }

                fun clearTrainingActivitiesTable() {
                    database.execSQL("DROP TABLE training_activities")

                    database.execSQL(
                        "CREATE TABLE training_activities (" +
                                "tId INTEGER NOT NULL, " +
                                "aId INTEGER NOT NULL, " +
                                "activityOrder INTEGER NOT NULL, " +
                                "PRIMARY KEY (tId, activityOrder))"
                    )
                }

                fun clearActivitiesTable() {
                    database.execSQL("DROP TABLE activity")

                    database.execSQL(
                        "CREATE TABLE activity (" +
                                "activityId INTEGER NOT NULL, " +
                                "name TEXT NOT NULL, " +
                                "duration INTEGER NOT NULL, " +
                                "activityType TEXT NOT NULL, " +
                                "PRIMARY KEY (activityId))"
                    )
                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_activity_name_duration ON activity(name, duration)")
                }

                fun addBreaksBetweenActivities(
                    trainingActivityEntities: List<TrainingActivityEntity>,
                    breakId: Long
                ) {
                    var index = 0
                    trainingActivityEntities.forEachIndexed { indexd, it ->
                        database.execSQL(
                            "INSERT INTO training_activities (tId, aId, activityOrder) " +
                                    "VALUES (${it.tId},${it.aId},$index)"
                        )
                        index++
                        if (it.tId == trainingActivityEntities.getOrNull(indexd + 1)?.tId) {
                            database.execSQL(
                                "INSERT INTO training_activities (tId, aId, activityOrder) " +
                                        "VALUES (${it.tId},$breakId,$index)"
                            )
                            index++
                        } else {
                            index = 0
                        }
                    }
                }

                fun getTrainingActivitiesList(): List<TrainingActivityEntity> {
                    val trainingActivityEntities: MutableList<TrainingActivityEntity> =
                        ArrayList()
                    val cursor = database.query("SELECT * FROM training_activities")

                    if (cursor.moveToFirst()) {
                        do {
                            val tId: Long = cursor.getLong(cursor.getColumnIndex("tId"))
                            val aId: Long = cursor.getLong(cursor.getColumnIndex("aId"))
                            val activityOrder: Int =
                                cursor.getInt(cursor.getColumnIndex("activityOrder"))
                            val trainingActivityEntity =
                                TrainingActivityEntity(tId, aId, activityOrder)

                            trainingActivityEntities.add(trainingActivityEntity)
                        } while (cursor.moveToNext())

                    }
                    return trainingActivityEntities
                }

                fun getAllActivities(): List<ActivityEntity> {
                    val activitiesEntities: MutableList<ActivityEntity> = mutableListOf()
                    val cursor = database.query("SELECT * from activity")
                    if (cursor.moveToFirst()) {
                        do {
                            val activityId: Long =
                                cursor.getLong(cursor.getColumnIndex("activityId"))
                            val name: String = cursor.getString(cursor.getColumnIndex("name"))
                            val duration: Int = cursor.getInt(cursor.getColumnIndex("duration"))
                            val activityType: String =
                                cursor.getString(cursor.getColumnIndex("activityType"))
                            val activityEntity =
                                ActivityEntity(
                                    activityId,
                                    name,
                                    duration,
                                    ActivityType.valueOf(activityType)
                                )

                            activitiesEntities.add(activityEntity)
                        } while (cursor.moveToNext())
                    }
                    return activitiesEntities
                }

                fun removeDuplicateActivities() {
                    fun ActivityEntity.isDuplicate(activityEntity: ActivityEntity) =
                        this.name == activityEntity.name && this.duration == activityEntity.duration

                    fun List<ActivityEntity>.containsDuplicate(activityEntity: ActivityEntity): List<Long> {
                        val duplicates: MutableList<Long> = mutableListOf()
                        this.forEach {
                            if (it.isDuplicate(activityEntity)) {
                                duplicates.add(it.activityId)
                            }
                        }
                        return duplicates
                    }

                    fun List<ActivityEntity>.containsActivity(activityEntity: ActivityEntity): Boolean {
                        this.forEach {
                            if (it.isDuplicate(activityEntity)) {
                                return true
                            }
                        }
                        return false
                    }

                    fun findDuplicates(activitiesList: List<ActivityEntity>): MutableMap<Long, List<Long>> {
                        val nonDuplicatesActivityList: MutableList<ActivityEntity> = mutableListOf()
                        val duplicatesMap: MutableMap<Long, List<Long>> = mutableMapOf()

                        activitiesList.forEach {
                            if (!nonDuplicatesActivityList.containsActivity(it)) {
                                val duplicates = activitiesList.containsDuplicate(it)
                                val duplicatesFilterIndexed =
                                    duplicates.filterIndexed { index, _ -> index != 0 }
                                if (duplicates.size > 1) {
                                    duplicatesMap.put(
                                        duplicates[0],
                                        duplicatesFilterIndexed
                                    )
                                    nonDuplicatesActivityList.add(it)
                                } else {
                                    nonDuplicatesActivityList.add(it)
                                }
                            }
                        }

                        nonDuplicatesActivityList.forEach {
                            database.execSQL(
                                "INSERT INTO activity (activityId,name,duration,activityType) " +
                                        "VALUES (${it.activityId},'${it.name}',${it.duration},'${it.activityType}') "
                            )
                        }
                        return duplicatesMap
                    }

                    fun removeDuplicatesFromRelation(duplicatesMapIds: MutableMap<Long, List<Long>>) {
                        duplicatesMapIds.forEach { duplicatesMapElement ->
                            duplicatesMapElement.value.forEach {
                                database.execSQL("UPDATE training_activities SET aId = ${duplicatesMapElement.key} WHERE aId = $it")
                            }
                        }
                    }

                    val activities = getAllActivities()
                    clearActivitiesTable()
                    removeDuplicatesFromRelation(findDuplicates(activities))
                }

                fun addBreaksToTrainings() {
                    val breakId = getBreakId()
                    if (breakId != null) {
                        val trainingActivityEntities = getTrainingActivitiesList()
                        clearTrainingActivitiesTable()
                        addBreaksBetweenActivities(trainingActivityEntities, breakId)
                        removeDuplicateActivities()
                    }
                }

                addActivityOrderColumn()
                addBreakActivity()
                changePrimaryKeys()
                addBreaksToTrainings()
            }
        }
    }
}