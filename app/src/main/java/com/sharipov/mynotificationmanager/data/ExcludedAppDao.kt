package com.sharipov.mynotificationmanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sharipov.mynotificationmanager.model.ExcludedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcludedAppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addExcludedApp(excludedApp: ExcludedAppEntity)

    @Query("SELECT * FROM excluded_apps")
    fun getAllExcludedApps(): Flow<List<ExcludedAppEntity>>

    @Query("SELECT * FROM excluded_apps WHERE packageName = :packageName")
    suspend fun getExcludedAppByPackageName(packageName: String): ExcludedAppEntity?

    @Query("DELETE FROM excluded_apps WHERE packageName = :packageName")
    suspend fun deleteExcludedAppByPackageName(packageName: String)

    @Query("SELECT * FROM excluded_apps WHERE appName LIKE '%' || :query || '%' ")
    fun searchApplication(query: String): Flow<List<ExcludedAppEntity>>

    @Update
    suspend fun updateExcludedApp(excludedApp: ExcludedAppEntity)
}