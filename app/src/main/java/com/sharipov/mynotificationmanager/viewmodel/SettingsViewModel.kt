package com.sharipov.mynotificationmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharipov.mynotificationmanager.data.repository.AppSettingsRepository
import com.sharipov.mynotificationmanager.data.repository.ExcludedAppRepository
import com.sharipov.mynotificationmanager.model.AppSettingsEntity
import com.sharipov.mynotificationmanager.model.ExcludedAppEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SettingsViewModelAbstract {

    suspend fun saveAppSettings(settings: AppSettingsEntity)

    suspend fun getAppSettings() : AppSettingsEntity?

    suspend fun updateSettings(settings: AppSettingsEntity)

    fun addExcludedApp(app: ExcludedAppEntity)

    fun getAllExcludedApps() : Flow<List<ExcludedAppEntity>>

    fun updateExcludedApp(app: ExcludedAppEntity)

    fun searchApplication(query: String): Flow<List<ExcludedAppEntity>>

    fun deleteExcludedAppByPackageName(packageName: String)

    fun setExcludedStatusForAllNotifications(isExcluded: Boolean)

    fun setBlockedStatusForAllNotifications(isBlocked: Boolean)
}


@HiltViewModel
class SettingsViewModel
@Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val excludedAppRepository: ExcludedAppRepository
): ViewModel(), SettingsViewModelAbstract  {

    override suspend fun saveAppSettings(settings: AppSettingsEntity) {
        appSettingsRepository.saveAppSettings(settings)
    }

    override suspend fun getAppSettings(): AppSettingsEntity? {
        return appSettingsRepository.getAppSettings()
    }

    override suspend fun updateSettings(settings: AppSettingsEntity) {
        appSettingsRepository.updateSettings(settings)
    }

    override fun getAllExcludedApps(): Flow<List<ExcludedAppEntity>> {
        return excludedAppRepository.getAllExcludedApps()
    }
    override  fun searchApplication(query: String): Flow<List<ExcludedAppEntity>> {
        return excludedAppRepository.searchApplication(query)
    }
    override fun addExcludedApp(app: ExcludedAppEntity) {
        viewModelScope.launch {
            excludedAppRepository.addExcludedApp(app)
        }
    }
    override fun updateExcludedApp(app: ExcludedAppEntity) {
        viewModelScope.launch {
            excludedAppRepository.updateExcludedApp(app = app)
        }
    }
    override fun deleteExcludedAppByPackageName(packageName: String) {
        viewModelScope.launch {
            excludedAppRepository.deleteExcludedAppByPackageName(packageName = packageName)
        }
    }
    override fun setExcludedStatusForAllNotifications(isExcluded: Boolean) {
        viewModelScope.launch {
            excludedAppRepository.setExcludedStatusForAllNotifications(isExcluded)
        }
    }
    override fun setBlockedStatusForAllNotifications(isBlocked: Boolean) {
        viewModelScope.launch {
            excludedAppRepository.setBlockedStatusForAllNotifications(isBlocked)
        }
    }
}