package io.mhdkhubbi.filo.viewmodels

import androidx.lifecycle.ViewModel
import io.mhdkhubbi.filo.datamodel.StorageStats
import io.mhdkhubbi.filo.domain.getStorageStats


class HomeScreenViewModel : ViewModel() {
    private val stats: StorageStats = getStorageStats()
    val usedBytes: Long = stats.usedBytes
    val totalBytes: Long = stats.totalBytes

    fun formatDecimalGB(bytes: Long): String {
        val gb = 1000.0 * 1000 * 1000 // decimal GB
        return String.format("%.1f GB", bytes / gb)
    }


    fun getUsagePercent(): Float {
        return if (totalBytes == 0L) 0f else usedBytes.toFloat() / totalBytes.toFloat()
    }

    fun getUsageLabel(): String {

        val percent = getUsagePercent() * 100
        return String.format("%.0f%%", percent)
    }
    fun formateUsagePercent(): String{
        return "${formatDecimalGB(stats.usedBytes)} of ${formatDecimalGB(stats.totalBytes)} Used"
    }


}