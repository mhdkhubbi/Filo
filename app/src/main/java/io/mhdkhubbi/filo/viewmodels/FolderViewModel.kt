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
            val result = listFilesInLight(path) // lightweight listing
            withContext(Dispatchers.Main) {
                _files.value = result
            }

            // launch background jobs for folder sizes
            result.filter { it.isDirectory }.forEach { entry ->
                if (!sizeCache.containsKey(entry.fullPath)) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val size = getFolderSize(Path(entry.fullPath))
                        sizeCache[entry.fullPath] = size
                        // update entry in list
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
}
