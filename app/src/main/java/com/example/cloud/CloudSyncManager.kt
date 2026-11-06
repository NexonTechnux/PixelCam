package com.example.cloud

import android.content.Context
import com.example.model.CloudBackupService
import com.example.model.CloudSyncStatus
import com.example.model.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class CloudAccount(
    val email: String = "semir.jetaime@gmail.com",
    val displayName: String = "Semir Jetaime",
    val isSignedIn: Boolean = true,
    val totalStorageGb: Float = 15.0f,
    val usedStorageGb: Float = 4.2f,
    val activeService: CloudBackupService = CloudBackupService.GOOGLE_PHOTOS,
    val autoBackupEnabled: Boolean = true
)

class CloudSyncManager(private val context: Context, private val scope: CoroutineScope) {

    private val _account = MutableStateFlow(CloudAccount())
    val account: StateFlow<CloudAccount> = _account.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncProgress = MutableStateFlow(0f)
    val syncProgress: StateFlow<Float> = _syncProgress.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    fun toggleAutoBackup(enabled: Boolean) {
        _account.value = _account.value.copy(autoBackupEnabled = enabled)
    }

    fun setBackupService(service: CloudBackupService) {
        _account.value = _account.value.copy(activeService = service)
    }

    fun signOut() {
        _account.value = _account.value.copy(
            isSignedIn = false,
            email = "",
            displayName = ""
        )
    }

    fun signInWithGoogle(email: String = "semir.jetaime@gmail.com", name: String = "Semir Jetaime") {
        _account.value = _account.value.copy(
            isSignedIn = true,
            email = email,
            displayName = name
        )
    }

    fun syncMediaItem(item: MediaItem, onComplete: (MediaItem) -> Unit) {
        if (!_account.value.isSignedIn || !_account.value.autoBackupEnabled) return

        scope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            _syncProgress.value = 0.2f
            delay(400)
            _syncProgress.value = 0.6f
            delay(500)
            _syncProgress.value = 1.0f
            _lastSyncTime.value = System.currentTimeMillis()
            _isSyncing.value = false

            val updatedUsedStorage = _account.value.usedStorageGb + 0.005f
            _account.value = _account.value.copy(usedStorageGb = updatedUsedStorage)

            onComplete(item.copy(cloudSyncStatus = CloudSyncStatus.SYNCED))
        }
    }

    fun syncAll(items: List<MediaItem>, onItemsUpdated: (List<MediaItem>) -> Unit) {
        if (!_account.value.isSignedIn) return

        scope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            val total = items.size
            if (total == 0) {
                _isSyncing.value = false
                return@launch
            }

            val updated = items.mapIndexed { idx, item ->
                delay(200)
                _syncProgress.value = (idx + 1).toFloat() / total
                item.copy(cloudSyncStatus = CloudSyncStatus.SYNCED)
            }
            _lastSyncTime.value = System.currentTimeMillis()
            _isSyncing.value = false
            onItemsUpdated(updated)
        }
    }
}
