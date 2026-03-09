package com.sharipov.mynotificationmanager.data.repository

import com.sharipov.mynotificationmanager.data.NotificationDao
import com.sharipov.mynotificationmanager.model.Application
import com.sharipov.mynotificationmanager.model.NotificationEntity
import com.sharipov.mynotificationmanager.model.UserGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class NotificationRepository(
    private val notificationDao: NotificationDao
) {
    fun getAllFlow(): Flow<List<NotificationEntity>> =
        notificationDao.getAllFlow()
    fun getAllNotificationsInGroup(group: String, packageName: String): Flow<List<NotificationEntity>> =
        notificationDao.getAllNotificationsInGroup(group, packageName)
    fun searchNotificationsInGroup(group: String, packageName: String, query: String): Flow<List<NotificationEntity>> =
        notificationDao.searchNotificationsInGroup(group, packageName, query)
    fun getAllUserNotifications(group: String, userName: String, packageName: String): Flow<List<NotificationEntity>> =
        notificationDao.getAllUserNotifications(group, userName, packageName)
    fun searchUserNotifications(group: String, userName: String, packageName: String, query: String): Flow<List<NotificationEntity>> =
        notificationDao.searchUserNotifications(group, userName, packageName, query)
    fun getFavoriteNotifications(): Flow<List<NotificationEntity>> =
        notificationDao.getFavoriteNotifications()
    fun getApplications(): Flow<List<Application>>  =
        notificationDao.getApplications()
    fun getApplicationsNotificationsCount(packageName: String) =
            notificationDao.getApplicationsCount(packageName).flowOn(Dispatchers.IO)

    fun searchNotifications(query: String): Flow<List<NotificationEntity>> =
        notificationDao.searchNotifications(query)

    fun getNotificationsFromData(fromDate: Long?, toDate: Long?): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsFromData(fromDate, toDate)
    fun getApplicationUserNames(packageName: String): Flow<List<UserGroup>> =
        notificationDao.getApplicationUserNames(packageName)
    fun getNotificationsByAppNames(packageName: List<String>): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsByAppNames(packageName)
    suspend fun deleteNotificationsForUser(group: String, user: String, packageName: String) =
        notificationDao.deleteNotificationsForUser(group, user, packageName)
    suspend fun deleteExpiredNotification(autoDeleteTimeout: Long) =
        notificationDao.deleteExpiredNotification(autoDeleteTimeout)
    suspend fun insert(notification: NotificationEntity) =
        notificationDao.insert(notification)
    suspend fun insertImportedNotifications(notifications: List<NotificationEntity>) =
        notificationDao.insertImportedNotifications(notifications)
    suspend fun upgrade(notification: NotificationEntity) =
        notificationDao.update(notification)
    suspend fun delete(notification: NotificationEntity) =
        notificationDao.delete(notification)
}
