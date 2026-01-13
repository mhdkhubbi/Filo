
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.domain.FileEntry
import io.mhdkhubbi.filo.domain.MediaType
import io.mhdkhubbi.filo.ui.theme.screens.FileScreen
import io.mhdkhubbi.filo.ui.theme.screens.HomeScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
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
    val files: List<FileEntry> = emptyList(),
    val sizeCache: Map<String, Long> = emptyMap(),
    // Selection & Operations
    val selectedPaths: Set<String> = emptySet(),
    val activeOperation: String? = null,
    val sourcePaths: Set<String> = emptySet(),
    val targetPath: String? = null,
    // UI Metadata
    val directoryName: String = "",
    val currentPath: String = "/storage/emulated/0",
    // Status Flags
    val isLoading: Boolean = false,
    val isFileActionInProgress: Boolean = false,
    val actionProgress: Float = 0f,
   // Dialog States
    val isDeletionDialogVisible: Boolean = false,
    val isCreateFolderDialogVisible: Boolean = false,
    val newFolderName: String = "",
    val folderNameError: String = "right",
    // Navigation & Helpers
    val shouldSyncNavigation: Boolean = false,
    val targetDeletionPath: String = "",
)
@SuppressLint("StaticFieldLeak")
class FileScreenViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(FileScreenUiState())
    val uiState: StateFlow<FileScreenUiState> = _uiState.asStateFlow()

    private fun updateState(transform: (FileScreenUiState) -> FileScreenUiState) {
        _uiState.value = transform(_uiState.value)
    }


    private val folderSizeQueue = Channel<String>(Channel.UNLIMITED)
    private val sizeUpdateBuffer = mutableMapOf<String, Long>()
    private val limitedIO = Dispatchers.IO.limitedParallelism(4)

    init {
        // WE ONLY START THE BUFFER LOOP HERE
        // We do NOT start the folderSizeQueue processing yet to save CPU at startup
        viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                delay(300)
                if (sizeUpdateBuffer.isNotEmpty()) applyBufferedSizes()
            }
        }
    }

    // NEW: A flag to ensure we only start the background worker once
    private var isWorkerStarted = false

    private fun startBackgroundWorker() {
        if (isWorkerStarted) return
        isWorkerStarted = true

        viewModelScope.launch(limitedIO) {
            for (path in folderSizeQueue) {
                // Check if it's a directory before deep scanning
                val file = java.io.File(path)
                if (file.isDirectory) {
                    val size = calculatePathSize(path, deepScan = true)
                    sizeUpdateBuffer[path] = size
                }
            }
        }
    }
//    init {
//        viewModelScope.launch(limitedIO) {
//            // Buffer observer loop
//            launch {
//                while (true) {
//                    delay(150) // Flush updates to UI every 300ms
//                    if (sizeUpdateBuffer.isNotEmpty()) applyBufferedSizes()
//                }
//            }
//
//            // Processing the queue
//            for (path in folderSizeQueue) {
//
//                val size = calculatePathSize(path, deepScan = true)
//
//                sizeUpdateBuffer[path] = size
//                if (sizeUpdateBuffer.size >= 5) {
//                    applyBufferedSizes()
//                }
//            }
//        }
//    }



    fun requestFolderSize(path: String) {
        folderSizeQueue.trySend(path)
    }
    private fun applyBufferedSizes() {
        // Create a thread-safe copy of the buffer
        val snapshot = synchronized(sizeUpdateBuffer) {
            val copy = sizeUpdateBuffer.toMap()
            sizeUpdateBuffer.clear()
            copy
        }

        if (snapshot.isEmpty()) return

        updateState { state ->
            val updated = state.files.map { entry ->
                snapshot[entry.path]?.let {
                    entry.copy(sizeBytes = it, formattedSize = formatSize(it))
                } ?: entry
            }
            state.copy(files = updated)
        }
    }

    fun loadMediaFolders(type: MediaType) {
        updateState { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val folders = getMediaFolders(context, type)

            updateState {
                it.copy(
                    files = folders,
                    isLoading = false,
                    currentPath = "media:$type"
                )
            }
        }
    }


    fun showDialogFlag(show: Boolean) {
        updateState { it.copy(isDeletionDialogVisible = show) }
    }
    fun folderDialogFlag(show: Boolean) {
        updateState { it.copy(isCreateFolderDialogVisible = show) }
    }
    fun copyItemFlag(path: String) {
        updateState { it.copy(activeOperation = "copy", sourcePaths = setOf(path)) }

    }

    fun moveItemFlag(path: String) {
        updateState { it.copy(activeOperation = "move", sourcePaths = setOf(path)) }

    }
    fun copyAllFlag() {
        val state = _uiState.value
        updateState {
            it.copy(
                activeOperation = "copy",
                sourcePaths = state.selectedPaths,
                selectedPaths = emptySet()
            )
        }

    }

    fun moveAllFlag() {
        val state = _uiState.value
        updateState {
            it.copy(
                activeOperation = "move",
                sourcePaths = state.selectedPaths,
                selectedPaths = emptySet()
            )
        }

    }
    fun clearNavigationResetFlag() {
        updateState { it.copy(shouldSyncNavigation = false) }
    }
    fun cancelActiveOperation() {
        updateState { it.copy(activeOperation = null, targetPath = null) }
    }

    fun resetNavigationTo(path: String, backStack: NavBackStack<NavKey>) {
        backStack.clear()
        backStack.add(HomeScreen)
        backStack.add(FileScreen(path))
    }

    fun folderNameChange(name: String) {
        updateState { it.copy(newFolderName = name) }
    }

    fun targetPathChange(newDestinationPath: String?) {
        updateState { it.copy(targetPath = newDestinationPath) }
    }
    fun deleteItemChange(deletedItem: String) {
        updateState { it.copy(targetDeletionPath = deletedItem) }
    }
    fun selectAll(files: List<FileEntry>) {
        updateState { it.copy(selectedPaths = files.map { f -> f.path }.toSet()) }
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
    fun addingFolder() {
        val state = _uiState.value
        val result = createFolder(Path(state.currentPath), state.newFolderName)
        if (result) {
            loadFiles(state.currentPath)
            updateState {
                it.copy(
                    folderNameError = "right",
                    newFolderName = "",
                    isCreateFolderDialogVisible = false
                )
            }
        } else {
            updateState { it.copy(folderNameError = "wrong") }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun deleteItem(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isFileActionInProgress = true, actionProgress = 0f) }

            deleteFileOrDirectory(Path(path))


            withContext(Dispatchers.Main) {
                for (i in 1..10) {
                    delay(10)
                    val p = i / 10f
                    updateState { it.copy(actionProgress = p) }
                }
            }

            loadFiles(_uiState.value.currentPath)
            updateState { it.copy(isFileActionInProgress = false, targetDeletionPath = "") }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val state = _uiState.value
            updateState { it.copy(isFileActionInProgress = true, actionProgress = 0f) }

            val total = state.selectedPaths.size
            var done = 0

            withContext(Dispatchers.Main) {
                state.selectedPaths.forEach { path ->
                    deleteFileOrDirectory(Path(path))
                    done++
                    val overall = done.toFloat() / total
                    updateState { it.copy(actionProgress = overall) }
                }
            }

            clearSelection()
            loadFiles(state.currentPath)
            updateState { it.copy(isFileActionInProgress = false) }
        }
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun executeActiveOperation() {
        val state = _uiState.value
        val dest = state.targetPath ?: return
        println("EXECUTE: pending=${state.activeOperation} dest=$dest selected=${state.selectedPaths.size}")

        when (state.activeOperation) {
            "copy" -> copyAll(Path(dest))
            "move" -> moveAll(Path(dest))
        }

        updateState {
            it.copy(
                activeOperation = null,
                targetPath = null,
                selectedPaths = emptySet()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun copyAll(targetDir: Path) {
        val state = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isFileActionInProgress = true, actionProgress = 0f) }

                val totalItems = state.sourcePaths.size
                var completedItems = 0

                for (src in state.sourcePaths) {
                    val srcPath = Path(src)
                    val destPath = targetDir.resolve(srcPath.name)

                    copyDirectoryOrFile(srcPath, destPath) { copied, total ->
                        val itemProgress = copied.toFloat() / total.toFloat()
                        val overall = (completedItems + itemProgress) / totalItems
                        updateState { it.copy(actionProgress = overall) }
                    }

                    completedItems++
                }

                withContext(Dispatchers.Main) {
                    updateState { it.copy(shouldSyncNavigation = true) }

                }


                loadFiles(state.currentPath)

            } catch (e: Exception) {
                println("PASTE CRASH: ${e.stackTraceToString()}")
            } finally {
                updateState { it.copy(isFileActionInProgress = false) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun moveAll(targetDir: Path) {
        val state = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isFileActionInProgress = true, actionProgress = 0f) }

                val totalItems = state.sourcePaths.size
                var completedItems = 0

                for (src in state.sourcePaths) {
                    val srcPath = Path(src)
                    val destPath = targetDir.resolve(srcPath.name)

                    moveDirectoryOrFile(srcPath, destPath) { copied, total ->
                        val itemProgress = copied.toFloat() / total.toFloat()
                        val overall = (completedItems + itemProgress) / totalItems
                        updateState { it.copy(actionProgress = overall) }
                    }

                    completedItems++
                }

                withContext(Dispatchers.Main) {
                    updateState { it.copy(shouldSyncNavigation = true) }

                }

                loadFiles(state.currentPath)

            } catch (e: Exception) {
                println("MOVE CRASH: ${e.stackTraceToString()}")
            } finally {
                updateState { it.copy(isFileActionInProgress = false) }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun loadFiles(path: String) {
        startBackgroundWorker()
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = true, currentPath = path) }

            // 1. Instant Listing (Filenames only)
            val entries = listFiles(path)

            // 2. Immediate UI Push - Use immediate to bypass the main loop queue
            withContext(Dispatchers.Main.immediate) {
                updateState {
                    it.copy(
                        files = entries,
                        isLoading = false,
                        directoryName = if (path == "/storage/emulated/0") "Internal Storage"
                        else path.substringAfterLast("/")
                    )
                }
            }

            // 3. Optimization: Only request sizes for common user folders first
            // Avoid slamming the disk with 50 background threads at once
            val priorityFolders = setOf("Download", "DCIM", "Documents", "Pictures", "Music", "Movies")

            // First pass: Priority folders
            entries.filter { it.isFolder && priorityFolders.contains(it.name) }.forEach {
                requestFolderSize(it.path)
            }

            // Small delay to let the UI finish animating
            delay(200)

            // Second pass: Everything else
            entries.filter { it.isFolder && !priorityFolders.contains(it.name) }.forEach {
                requestFolderSize(it.path)
            }
        }
    }




}