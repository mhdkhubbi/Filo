package io.mhdkhubbi.filo.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.mhdkhubbi.filo.datamodel.StorageStats
import io.mhdkhubbi.filo.domain.formatSize
import io.mhdkhubbi.filo.domain.getStorageStats
import io.mhdkhubbi.filo.domain.listFilesIn


class HomeScreenViewModel : ViewModel() {
    var files by mutableStateOf(listFilesIn("/storage/emulated/0"))


    private val stats: StorageStats = getStorageStats()

    val usedBytes: Long = stats.usedBytes
    val totalBytes: Long = stats.totalBytes



    fun getUsagePercent(): Float {
        return if (totalBytes == 0L) 0f else usedBytes.toFloat() / totalBytes.toFloat()
    }

    fun getUsageLabel(): String {

        val percent = getUsagePercent() * 100
        return String.format("%.0f%%", percent)
    }
    fun formateUsagePercent(): String{
        return "${formatSize(usedBytes)} of ${formatSize(totalBytes)} Used"
    }

}