import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mhdkhubbi.filo.domain.FsEntry
import io.mhdkhubbi.filo.domain.formatSize
import io.mhdkhubbi.filo.domain.getFolderSize
import io.mhdkhubbi.filo.domain.listFilesInLight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.io.path.Path
import kotlin.io.path.name

class FileScreenViewModel : ViewModel() {

    // Stable, in-place mutable state list to avoid replacing the whole list
    private val _files = mutableStateListOf<FsEntry>()
    val files: List<FsEntry> get() = _files

    // Cache sizes so they don’t reset to 0 when you revisit
    private val sizeCache = mutableMapOf<String, Long>()
    var isLoading by mutableStateOf(false)
        private set
    var fileName by mutableStateOf("")
        private set

    fun loadFiles(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            // Lightweight listing without recursive sizes
            val base = listFilesInLight(path, sizeCache)
                .filter { !Path(it.fullPath).name.startsWith(".") }
                .sortedWith(
                    compareBy<FsEntry> { !it.isDirectory } // folders first
                        .thenBy { it.name.lowercase() }     // then alphabetical
                )

            // Apply cached sizes without changing order
            val withSizes = base.map { e ->
                if (e.isDirectory) {
                    val cached = sizeCache[e.fullPath]
                    if (cached != null) {
                        e.copy(sizeBytes = cached, sizeMega = formatSize(cached))
                    } else e
                } else e
            }

            withContext(Dispatchers.Main) {
                _files.clear()
                _files.addAll(withSizes)
                isLoading = false // replace content, keep order
                fileName=if(path=="/storage/emulated/0") "Internal Storage"
                else path.substringAfterLast("/")
            }

            // Compute missing folder sizes asynchronously, update in place
            withSizes.forEach { entry ->
                if (entry.isDirectory && sizeCache[entry.fullPath] == null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val size = getFolderSize(Path(entry.fullPath))
                        sizeCache[entry.fullPath] = size
                        withContext(Dispatchers.Main) {
                            val idx = _files.indexOfFirst { it.fullPath == entry.fullPath }
                            if (idx >= 0) {
                                _files[idx] = _files[idx].copy(
                                    sizeBytes = size,
                                    sizeMega = formatSize(size)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}