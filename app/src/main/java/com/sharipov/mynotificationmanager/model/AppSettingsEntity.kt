package com.sharipov.mynotificationmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 0,
    val autoDeleteTimeoutLong: Long,
    val autoDeleteTimeoutString: String
)
