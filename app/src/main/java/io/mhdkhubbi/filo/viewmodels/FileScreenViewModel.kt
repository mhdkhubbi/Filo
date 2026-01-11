import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.domain.FsEntry
import io.mhdkhubbi.filo.ui.theme.screens.FileScreen
import io.mhdkhubbi.filo.ui.theme.screens.HomeScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

data class FileScreenUiState(
    val files: List<FsEntry> = emptyList(),
    val sizeCache: Map<String, Long> = emptyMap(),
    val selectedPaths: Set<String> = emptySet(),
    val pendingOperation: String? = null,
    val operationPaths: Set<String> = emptySet(),
    val fileName: String = "",
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val isProcessing: Boolean = false,
    val progress: Float = 0f,
    val currentPath: String = "/storage/emulated/0",
    val destinationPath: String? = null,
    val shouldResetNavigation: Boolean = false,
    val createFolderDialog: Boolean = false,
    val folderNameToAdd: String = "",
    val wrongName: String = "right",
    val deletedOne: String = ""
)

class FileScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FileScreenUiState())
    val uiState: StateFlow<FileScreenUiState> = _uiState.asStateFlow()

    // Helper to update state immutably
    private fun updateState(transform: (FileScreenUiState) -> FileScreenUiState) {
        _uiState.value = transform(_uiState.value)
    }

    fun folderNameChange(name: String) {
        updateState { it.copy(folderNameToAdd = name) }
    }

    fun ShowDialogChange(show: Boolean) {
        updateState { it.copy(showDialog = show) }

    }

    fun CreateFolderDialogChange(show: Boolean) {


        updateState { it.copy(createFolderDialog = show) }


    }

    fun changedestinationPath(newdestinationPath: String?) {
        updateState { it.copy(destinationPath = newdestinationPath) }
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun addingFolder() {
        val state = _uiState.value
        val result = createFolderIfNotExists(Path(state.currentPath), state.folderNameToAdd)
        if (result) {
            loadFiles(state.currentPath)
            updateState {
                it.copy(
                    wrongName = "right",
                    folderNameToAdd = "",
                    createFolderDialog = false
                )
            }
        } else {
            updateState { it.copy(wrongName = "wrong") }
        }
    }

    fun onCopyItem(path: String) {
        updateState { it.copy(pendingOperation = "copy", operationPaths = setOf(path)) }
    }

    fun onMoveItem(path: String) {
        updateState { it.copy(pendingOperation = "move", operationPaths = setOf(path)) }
    }

    fun selectAll(files: List<FsEntry>) {
        updateState { it.copy(selectedPaths = files.map { f -> f.fullPath }.toSet()) }
    }

    fun clearSelection() {
        updateState { it.copy(selectedPaths = emptySet()) }
    }

    fun toggleSelection(path: String) {
        val state = _uiState.value
        val newSet = if (state.selectedPaths.contains(path)) {
            state.selectedPaths - path
        } else {
            state.selectedPaths + path
        }
        updateState { it.copy(selectedPaths = newSet) }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun deleteOne(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isProcessing = true, progress = 0f) }

            deleteFileOrDirectory(Path(path))

            for (i in 1..10) {
                delay(10)
                val p = i / 10f
                updateState { it.copy(progress = p) }
            }

            loadFiles(_uiState.value.currentPath)
            updateState { it.copy(isProcessing = false, deletedOne = "") }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun deleteSelected() {
        viewModelScope.launch(Dispatchers.IO) {
            val state = _uiState.value
            updateState { it.copy(isProcessing = true, progress = 0f) }

            val total = state.selectedPaths.size
            var done = 0

            state.selectedPaths.forEach { path ->
                deleteFileOrDirectory(Path(path))
                done++
                val overall = done.toFloat() / total
                updateState { it.copy(progress = overall) }
            }

            clearSelection()
            loadFiles(state.currentPath)
            updateState { it.copy(isProcessing = false) }
        }
    }

    fun onCopy() {
        val state = _uiState.value
        updateState {
            it.copy(
                pendingOperation = "copy",
                operationPaths = state.selectedPaths,
                selectedPaths = emptySet()
            )
        }
    }

    fun onMove() {
        val state = _uiState.value
        updateState {
            it.copy(
                pendingOperation = "move",
                operationPaths = state.selectedPaths,
                selectedPaths = emptySet()
            )
        }
    }

    fun cancelPendingOperation() {
        updateState { it.copy(pendingOperation = null, destinationPath = null) }
    }

    fun clearNavigationResetFlag() {
        updateState { it.copy(shouldResetNavigation = false) }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun executePendingOperation() {
        val state = _uiState.value
        val dest = state.destinationPath ?: return
        println("EXECUTE: pending=${state.pendingOperation} dest=$dest selected=${state.selectedPaths.size}")

        when (state.pendingOperation) {
            "copy" -> copySelectedWithProgress(Path(dest))
            "move" -> moveSelectedWithProgress(Path(dest))
        }

        updateState {
            it.copy(
                pendingOperation = null,
                destinationPath = null,
                selectedPaths = emptySet()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun copySelectedWithProgress(targetDir: Path) {
        val state = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isProcessing = true, progress = 0f) }

                val totalItems = state.operationPaths.size
                var completedItems = 0

                for (src in state.operationPaths) {
                    val srcPath = Path(src)
                    val destPath = targetDir.resolve(srcPath.name)

                    copyWithProgress(srcPath, destPath) { copied, total ->
                        val itemProgress = copied.toFloat() / total.toFloat()
                        val overall = (completedItems + itemProgress) / totalItems
                        updateState { it.copy(progress = overall) }
                    }

                    completedItems++
                }

                withContext(Dispatchers.Main) {
                    updateState { it.copy(shouldResetNavigation = true) }
                }

                loadFiles(state.currentPath)

            } catch (e: Exception) {
                println("PASTE CRASH: ${e.stackTraceToString()}")
            } finally {
                updateState { it.copy(isProcessing = false) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun moveSelectedWithProgress(targetDir: Path) {
        val state = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isProcessing = true, progress = 0f) }

                val totalItems = state.operationPaths.size
                var completedItems = 0

                for (src in state.operationPaths) {
                    val srcPath = Path(src)
                    val destPath = targetDir.resolve(srcPath.name)

                    moveWithProgress(srcPath, destPath) { copied, total ->
                        val itemProgress = copied.toFloat() / total.toFloat()
                        val overall = (completedItems + itemProgress) / totalItems
                        updateState { it.copy(progress = overall) }
                    }

                    completedItems++
                }

                withContext(Dispatchers.Main) {
                    updateState { it.copy(shouldResetNavigation = true) }
                }

                loadFiles(state.currentPath)

            } catch (e: Exception) {
                println("MOVE CRASH: ${e.stackTraceToString()}")
            } finally {
                updateState { it.copy(isProcessing = false) }
            }
        }
    }


    fun resetNavigationTo(path: String, backStack: NavBackStack<NavKey>) {
        backStack.clear()
        backStack.add(HomeScreen)
        backStack.add(FileScreen(path))
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun loadFiles(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(currentPath = path,
             //   isLoading = true
            ) }

            val base = listFilesInLight(path, _uiState.value.sizeCache)
                .filter { !Path(it.fullPath).name.startsWith(".") }
                .sortedWith(
                    compareBy<FsEntry> { !it.isDirectory }
                        .thenBy { it.name.lowercase() }
                )

            val withSizes = base.map { e ->
                if (e.isDirectory) {
                    val cached = _uiState.value.sizeCache[e.fullPath]
                    if (cached != null) {
                        e.copy(sizeBytes = cached, sizeMega = formatSize(cached))
                    } else e
                } else e
            }

            withContext(Dispatchers.Main) {
                updateState {
                    it.copy(
                        files = withSizes,
                      //  isLoading = false,
                        fileName = if (path == "/storage/emulated/0") "Internal Storage"
                        else path.substringAfterLast("/")
                    )
                }
            }

            // Async folder size calculation
            withSizes.forEach { entry ->
                if (entry.isDirectory && _uiState.value.sizeCache[entry.fullPath] == null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val size = getFolderSize(Path(entry.fullPath))
                        val newCache = _uiState.value.sizeCache + (entry.fullPath to size)
                        withContext(Dispatchers.Main) {
                            val idx =
                                _uiState.value.files.indexOfFirst { it.fullPath == entry.fullPath }
                            if (idx >= 0) {
                                val updatedFiles = _uiState.value.files.toMutableList()
                                updatedFiles[idx] = updatedFiles[idx].copy(
                                    sizeBytes = size,
                                    sizeMega = formatSize(size)
                                )
                                updateState { it.copy(files = updatedFiles, sizeCache = newCache) }
                            }
                        }
                    }
                }
            }
        }
    }
}