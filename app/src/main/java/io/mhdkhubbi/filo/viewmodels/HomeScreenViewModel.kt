package io.mhdkhubbi.filo.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import getStorageStats
import io.mhdkhubbi.filo.domain.StorageVolume
import java.util.Locale

class HomeScreenViewModel(context: Context) : ViewModel() {

    // All detected storages (internal + optional SD card)
    val stats: List<StorageVolume> = getStorageStats(context)

    // Convenience: internal storage is always index 0
    private val internal: StorageVolume = stats.first()

    fun formatDecimalGB(bytes: Long): String {
        // 1024.0 → matches "56 GB" look of system settings
        // 1000.0 → matches "64 GB" look of the box
        val gb = 1024.0 * 1024 * 1024
        val locale = Locale("en", "US")
        return String.format(locale, "%.1f GB", bytes / gb)
    }

    fun getUsagePercent(storage: StorageVolume = internal): Float {
        return if (storage.totalBytes == 0L) 0f
        else storage.usedBytes.toFloat() / storage.totalBytes.toFloat()
    }

    fun getUsageLabel(storage: StorageVolume = internal): String {
        val locale = Locale("en", "US")
        val percent = getUsagePercent(storage) * 100
        return String.format(locale, "%.0f%%", percent)
    }

    fun formatUsagePercent(storage: StorageVolume = internal): String {
        return "${formatDecimalGB(storage.usedBytes)} of ${formatDecimalGB(storage.totalBytes)} Used"
    }
}