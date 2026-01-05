package io.mhdkhubbi.filo.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mhdkhubbi.filo.domain.FsEntry
import io.mhdkhubbi.filo.domain.getFolderSize
import io.mhdkhubbi.filo.domain.listFilesInLight

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.io.path.Path


class FolderViewModel : ViewModel() {
    private val _files = mutableStateOf(emptyList<FsEntry>())
    val files: List<FsEntry> get() = _files.value

    private val sizeCache = mutableMapOf<String, Long>()

    fun loadFiles(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = listFilesInLight(path, sizeCache)
            withContext(Dispatchers.Main) {
                _files.value = result
            }

            // calculate missing sizes in background
            result.filter { it.isDirectory && !sizeCache.containsKey(it.fullPath) }
                .forEach { entry ->
                    viewModelScope.launch(Dispatchers.IO) {
                        val size = getFolderSize(Path(entry.fullPath))
                        sizeCache[entry.fullPath] = size
                        withContext(Dispatchers.Main) {
                            _files.value = _files.value.map {
                                if (it.fullPath == entry.fullPath) it.copy(sizeBytes = size) else it
                            }
                        }
                    }
                }
        }
    }
}
