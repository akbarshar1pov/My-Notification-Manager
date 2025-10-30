package com.sharipov.mynotificationmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharipov.mynotificationmanager.data.repository.NotificationRepository
import com.sharipov.mynotificationmanager.model.Application
import com.sharipov.mynotificationmanager.model.NotificationEntity
import com.sharipov.mynotificationmanager.model.UserGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface HomeViewModelAbstract {

    val notificationListFlow: Flow<List<NotificationEntity>>
    fun getAllNotificationsInGroup(group: String, packageName: String): Flow<List<NotificationEntity>>
    fun searchNotificationsInGroup(group: String, packageName: String, query: String): Flow<List<NotificationEntity>>
    fun getAllUserNotifications(group: String, userName: String, packageName: String): Flow<List<NotificationEntity>>
    fun searchUserNotifications(group: String, userName: String, packageName: String, query: String): Flow<List<NotificationEntity>>
    fun getApplications(): Flow<List<Application>>
    fun getApplicationsNotificationsCount(packageName: String): Flow<Int>
    fun getApplicationUserNames(packageName: String): Flow<List<UserGroup>>
    fun getNotificationsByAppNames(packageName: List<String>): Flow<List<NotificationEntity>>
    fun getFavoriteNotifications(): Flow<List<NotificationEntity>>
    fun searchNotifications(query: String): Flow<List<NotificationEntity>>
    fun getNotificationsFromData(fromDate: Long?, toDate: Long?): Flow<List<NotificationEntity>>
    fun addNotification(notification: NotificationEntity)
    fun upgradeNotification(notification: NotificationEntity)
    fun deleteNotification(notification: NotificationEntity)
    fun deleteNotificationsForUser(group: String, user: String, packageName: String)
    fun deleteExpiredNotification(autoDeleteTimeout: Long)
}

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel(), HomeViewModelAbstract {

    override val notificationListFlow: Flow<List<NotificationEntity>> =
        notificationRepository.getAllFlow()

    override fun getAllNotificationsInGroup(group: String, packageName: String) =
        notificationRepository.getAllNotificationsInGroup(group, packageName)

    override fun searchNotificationsInGroup(group: String, packageName: String, query: String) =
        notificationRepository.searchNotificationsInGroup(group, packageName, query)

    override fun getAllUserNotifications(group: String, userName: String, packageName: String) =
        notificationRepository.getAllUserNotifications(group, userName, packageName)

    override fun searchUserNotifications(group: String, userName: String, packageName: String, query: String) =
        notificationRepository.searchUserNotifications(group, userName, packageName, query)

    override fun getApplications() =
        notificationRepository.getApplications()

    override fun getApplicationsNotificationsCount(packageName: String) =
        notificationRepository.getApplicationsNotificationsCount(packageName)

    override fun getFavoriteNotifications() =
        notificationRepository.getFavoriteNotifications()

    override fun searchNotifications(query: String): Flow<List<NotificationEntity>> =
        notificationRepository.searchNotifications(query)

    override fun getNotificationsFromData(fromDate: Long?, toDate: Long?): Flow<List<NotificationEntity>> =
        notificationRepository.getNotificationsFromData(fromDate, toDate)

    override fun getApplicationUserNames(packageName: String) =
        notificationRepository.getApplicationUserNames(packageName)

    override fun getNotificationsByAppNames(packageName: List<String>) =
        notificationRepository.getNotificationsByAppNames(packageName)

    override fun addNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            notificationRepository.insert(notification)
        }
    }

    override fun upgradeNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            notificationRepository.upgrade(notification)
        }
    }

    override fun deleteNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            notificationRepository.delete(notification)
        }
    }

    override fun deleteNotificationsForUser(group: String, user: String, packageName: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotificationsForUser(group, user, packageName)
        }
    }

    override fun deleteExpiredNotification(autoDeleteTimeout: Long) {
        viewModelScope.launch {
            notificationRepository.deleteExpiredNotification(autoDeleteTimeout)
        }
    }
}