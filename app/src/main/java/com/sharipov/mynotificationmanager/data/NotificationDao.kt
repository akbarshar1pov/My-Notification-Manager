package com.sharipov.mynotificationmanager.data

import androidx.room.*
import com.sharipov.mynotificationmanager.model.Application
import com.sharipov.mynotificationmanager.model.NotificationEntity
import com.sharipov.mynotificationmanager.model.UserGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    // Get all notifications in reverse order
    @Query("SELECT * FROM notification ORDER BY id DESC")
    fun getAllFlow(): Flow<List<NotificationEntity>>

    // Get all notifications for a specific user and package in reverse order
    @Query(
        "SELECT * FROM notification WHERE `group` = :group " +
                "AND user = :user " +
                "AND packageName = :packageName " +
                "ORDER BY id DESC"
    )
    fun getAllUserNotifications(group: String, user: String, packageName: String): Flow<List<NotificationEntity>>

    // Search for notifications by text for a specific user and package in reverse order
    @Query(
        "SELECT * FROM notification WHERE `group` = :group " +
                "AND packageName = :packageName " +
                "AND text LIKE '%' || :query || '%' " +
                "ORDER BY id DESC"
    )
    fun searchNotificationsInGroup(group: String, packageName: String, query: String): Flow<List<NotificationEntity>>

    // Get all notifications for a specific group and package in reverse order
    @Query(
        "SELECT * FROM notification WHERE `group` = :group " +
                "AND packageName = :packageName " +
                "ORDER BY id DESC"
    )
    fun getAllNotificationsInGroup(group: String, packageName: String): Flow<List<NotificationEntity>>

    // Search for notifications by text for a specific group, user and package in reverse order
    @Query(
        "SELECT * FROM notification WHERE `group` = :group AND user = :user " +
                "AND packageName = :packageName " +
                "AND text LIKE '%' || :query || '%' " +
                "ORDER BY id DESC"
    )
    fun searchUserNotifications(
        group: String,
        user: String,
        packageName: String,
        query: String
    ): Flow<List<NotificationEntity>>

    // Search for notifications by text, user, or package name
    @Query(
        "SELECT * FROM notification WHERE " +
                "`group` LIKE '%' || :query || '%' OR " +
                "text LIKE '%' || :query || '%' OR " +
                "user LIKE '%' || :query || '%' OR " +
                "packageName LIKE '%' || :query || '%'"
    )
    fun searchNotifications(query: String): Flow<List<NotificationEntity>>

    @Query(
        "SELECT * FROM notification WHERE " +
                "(:fromDate IS NULL OR time >= :fromDate) AND " +
                "(:toDate IS NULL OR time <= :toDate) ORDER BY id DESC"
    )
    fun getNotificationsFromData(fromDate: Long?, toDate: Long?): Flow<List<NotificationEntity>>

    // Get all favorite notifications
    @Query("SELECT * FROM notification WHERE favorite = 1")
    fun getFavoriteNotifications(): Flow<List<NotificationEntity>>

    // Get all distinct application package names
    @Query("SELECT DISTINCT notification.packageName, notification.appName FROM notification")
    fun getApplications(): Flow<List<Application>>

    // Get application notification count
    @Query("SELECT COUNT(*) FROM notification WHERE packageName = :packageName")
    fun getApplicationsCount(packageName: String): Flow<Int>

    // Get all notifications for a specific application package
    @Query("SELECT DISTINCT user, `group` FROM notification WHERE packageName = :packageName")
    fun getApplicationUserNames(packageName: String): Flow<List<UserGroup>>

    // Get all notifications for a specific applications
    @Query("SELECT * FROM notification WHERE packageName IN (:packageName)")
    fun getNotificationsByAppNames(packageName: List<String>): Flow<List<NotificationEntity>>

    // Check if a notification already exists for a specific user, text, package name, and app name
    @Query(
        "SELECT COUNT(*) FROM notification WHERE user = :user " +
                "AND text = :text " +
                "AND packageName = :packageName " +
                "AND appName = :appName"
    )
    suspend fun checkNotificationExists(user: String, text: String, packageName: String, appName: String): Int

    // Delete all notifications for a specific user and package
    @Query("DELETE FROM notification WHERE `group` = :group AND user = :user AND packageName = :packageName")
    suspend fun deleteNotificationsForUser(group: String, user: String, packageName: String)

    // Auto-delete notifications older than time limit
    @Query("DELETE FROM notification WHERE time < :autoDeleteTimeout AND favorite != 1")
    suspend fun deleteExpiredNotification(autoDeleteTimeout: Long)

    // Insert a new notification
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Transaction
    suspend fun insertImportedNotifications(notifications: List<NotificationEntity>) {
        notifications.forEach { notification ->
            val exists = checkNotificationExists(
                user = notification.user,
                text = notification.text,
                packageName = notification.packageName,
                appName = notification.appName
            )
            if (exists == 0) {
                insert(notification.copy(id = null))
            }
        }
    }

    // Update an existing notification
    @Update
    suspend fun update(notification: NotificationEntity)

    // Delete a notification
    @Delete
    suspend fun delete(notification: NotificationEntity)
}
