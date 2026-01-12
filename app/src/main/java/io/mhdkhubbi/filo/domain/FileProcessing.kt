
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import io.mhdkhubbi.filo.domain.FileEntry
import io.mhdkhubbi.filo.domain.FileType
import io.mhdkhubbi.filo.domain.MediaType
import io.mhdkhubbi.filo.domain.StorageVolume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.util.Locale

// -------------------- LISTING --------------------

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun listFiles(
    path: String,
): List<FileEntry> {
    val root = Path.of(path)

    return Files.newDirectoryStream(root).use { stream ->
        stream.asSequence()
            .filter { !it.fileName.toString().startsWith(".") }
            .map { entry ->
                val full = entry.toString()
                val isDir = Files.isDirectory(entry)
                val sizeBytes = if (isDir) 0L else File(full).length()
                val sizeMega = if (isDir) "--" else formatSize(sizeBytes)

                FileEntry(
                    name = entry.fileName.toString(),
                    path = full,
                    isFolder = isDir,
                    type = detectType(entry),
                    sizeBytes = sizeBytes,
                    childrenCount =if (isDir) countVisibleItems(full) else 0,
                    formattedSize = sizeMega
                )
            }
            .toList()
    }
}

fun getMediaFolders(context: Context, type: MediaType): List<FileEntry> {
    val projection = arrayOf(
        MediaStore.MediaColumns.DATA
    )
    val uri = when (type) {
        MediaType.IMAGES -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        MediaType.VIDEOS -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        MediaType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        MediaType.DOWNLOADS -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
        MediaType.DOCUMENTS -> MediaStore.Files.getContentUri("external")
    }

    // MIME filter for documents
    val selection = when (type) {
        MediaType.DOCUMENTS -> (
                "${MediaStore.Files.FileColumns.MIME_TYPE} IN (" +
                        "'application/pdf'," +
                        "'application/msword'," +
                        "'application/vnd.openxmlformats-officedocument.wordprocessingml.document'," +
                        "'application/vnd.ms-excel'," +
                        "'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'," +
                        "'application/vnd.ms-powerpoint'," +
                        "'application/vnd.openxmlformats-officedocument.presentationml.presentation'," +
                        "'text/plain'" +
                        ")"
                )
        else -> null
    }

    val cursor = context.contentResolver.query(
        uri,
        projection,
        selection,
        null,
        null
    )

    val folderMap = LinkedHashMap<String, MutableList<String>>()

    cursor?.use {
        val dataIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (it.moveToNext()) {
            val filePath = it.getString(dataIndex) ?: continue
            val folderPath = File(filePath).parent ?: continue

            val cleanPath = folderPath.replace("//", "/").trimEnd('/')

            folderMap.getOrPut(cleanPath) { mutableListOf() }.add(filePath)
        }
    }
    val result= folderMap.map { (folderPath, filesInFolder) ->
        val folderName = File(folderPath).name
        val totalSize = filesInFolder.sumOf { File(it).length() }
        val sizeMega = "%.2f MB".format(totalSize / 1024f / 1024f)

        FileEntry(
            name = folderName,
            path = folderPath,
            isFolder = true,
            type = FileType.FOLDER,
            sizeBytes = totalSize,
            childrenCount = filesInFolder.size,
            formattedSize = sizeMega
        )

    }

    return result
}

// -------------------- COPY / MOVE / DELETE / CREATE --------------------
suspend fun copyFile(
    src: Path,
    dest: Path,
    onProgress: (Long, Long) -> Unit
) = withContext(Dispatchers.IO) {
    val total = Files.size(src)
    var copied = 0L
    val buffer = ByteArray(64 * 1024)

    Files.createDirectories(dest.parent)
    Files.newInputStream(src).use { input ->
        Files.newOutputStream(dest).use { output ->
            var bytes = input.read(buffer)
            while (bytes >= 0) {
                output.write(buffer, 0, bytes)
                copied += bytes
                onProgress(copied, total)
                bytes = input.read(buffer)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
suspend fun copyDirectory(
    src: Path,
    dest: Path,
    onProgress: (Long, Long) -> Unit
) = withContext(Dispatchers.IO) {
    val buffer = ByteArray(64 * 1024)
    var copiedBytes = 0L

    val allFiles = Files.walk(src).filter { Files.isRegularFile(it) }.toList()
    val totalBytes = allFiles.sumOf { Files.size(it) }

    Files.walk(src).use { stream ->
        stream.forEach { path ->
            val relative = src.relativize(path)
            val target = dest.resolve(relative)
            if (Files.isDirectory(path)) {
                Files.createDirectories(target)
            } else {
                Files.newInputStream(path).use { input ->
                    Files.newOutputStream(target).use { output ->
                        var bytes = input.read(buffer)
                        while (bytes >= 0) {
                            output.write(buffer, 0, bytes)
                            copiedBytes += bytes
                            onProgress(copiedBytes, totalBytes)
                            bytes = input.read(buffer)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
suspend fun copyDirectoryOrFile(src: Path, dest: Path, onProgress: (Long, Long) -> Unit) {
    if (Files.isDirectory(src)) {
        copyDirectory(src, dest, onProgress)
    } else {
        copyFile(src, dest, onProgress)
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
suspend fun moveDirectoryOrFile(src: Path, dest: Path, onProgress: (Long, Long) -> Unit) {
    try {
        Files.createDirectories(dest.parent)
        Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING)
        onProgress(1, 1) // instant complete
    } catch (_: Exception) {
        copyDirectoryOrFile(src, dest, onProgress)
        deleteFileOrDirectory(src)
    }
}

suspend fun deleteFileOrDirectory(path: Path) = withContext(Dispatchers.IO) {
    if (Files.notExists(path)) return@withContext
    Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            Files.deleteIfExists(file)
            return FileVisitResult.CONTINUE
        }
        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
            Files.deleteIfExists(dir)
            return FileVisitResult.CONTINUE
        }
    })
}
fun createFolder(parent: Path, folderName: String): Boolean {
    val newFolder = parent.resolve(folderName)

    return try {
        if (Files.exists(newFolder)) {
            false // folder already exists
        } else {
            Files.createDirectories(newFolder)
            true // folder created
        }
    } catch (_: Exception) {
        false
    }
}

// -------------------- HELPERS --------------------

fun detectType(entry: Path): FileType {
    val name = entry.fileName.toString().lowercase()
    return when {
        Files.isDirectory(entry) -> FileType.FOLDER
        name.endsWith(".pdf") -> FileType.PDF
        name.endsWith(".apk") -> FileType.APK
        name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".gif") -> FileType.IMAGE
        name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi") -> FileType.VIDEO
        name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aac") -> FileType.AUDIO
        else -> FileType.OTHER
    }
}

@SuppressLint("DefaultLocale")
fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"

    val locale = Locale("en", "US")

    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        bytes >= gb -> String.format(locale, "%.2f GB", bytes / gb)
        bytes >= mb -> String.format(locale, "%.2f MB", bytes / mb)
        bytes >= kb -> String.format(locale, "%.2f KB", bytes / kb)
        else -> String.format(locale, "%d B", bytes)
    }
}
fun countVisibleItems(path: String): Int {
    val folder = File(path)
    if (!folder.exists() || !folder.isDirectory) return 0

    return folder.listFiles()?.count { !it.name.startsWith(".") } ?: 0
}
fun getAdvertisedStorage(totalBytes: Long): String {
    val gbDecimal = totalBytes / 1_000_000_000.0

    return when {
        // If the user partition is > 90GB, it's definitely a 128GB+ phone
        gbDecimal >= 90 -> "128 GB"
        // If the user partition is > 45GB, it's a 64GB phone (OS took ~15GB)
        gbDecimal >= 45 -> "64 GB"
        // If the user partition is > 20GB, it's a 32GB phone
        gbDecimal >= 20 -> "32 GB"
        // If the user partition is > 10GB, it's a 16GB phone
        gbDecimal >= 10 -> "16 GB"
        else -> "${gbDecimal.toInt()} GB"
    }
}


fun getStorageStats(context: Context): List<StorageVolume> {
    val result = mutableListOf<StorageVolume>()

    // --- Internal storage ---
    val internalPath = Environment.getExternalStorageDirectory().path
    result.add(calculateStats(internalPath, isSdCard = false))

    // --- SD card (if present) ---
    val dirs = context.getExternalFilesDirs(null)
    if (dirs.size > 1 && dirs[1] != null) {
        val sdCardFile = dirs[1]
        val absolutePath = sdCardFile.absolutePath
        val sdCardRootPath = absolutePath.split("/Android")[0]
        result.add(calculateStats(sdCardRootPath, isSdCard = true))
    }

    return result
}

private fun calculateStats(path: String, isSdCard: Boolean): StorageVolume {
    val stat = StatFs(path)
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val availableBlocks = stat.availableBlocksLong

    val totalBytes = totalBlocks * blockSize
    val availableBytes = availableBlocks * blockSize
    val usedBytes = totalBytes - availableBytes

    val percentUsed = if (totalBytes > 0) {
        (usedBytes.toDouble() / totalBytes.toDouble() * 100).toInt()
    } else 0

    return StorageVolume(
        usedBytes = usedBytes,
        totalBytes = totalBytes,
        freeBytes = availableBytes,
        usagePercent = percentUsed,
        totalCapacity = getAdvertisedStorage(totalBytes),
        isExternal = isSdCard,
        rootPath = path
    )
}



/**
 * Calculates size of a path.
 * - If it's a file, returns size immediately.
 * - If it's a folder, uses a non-recursive sum for speed.
 */
fun calculatePathSize(path: String, deepScan: Boolean = false): Long {
    val file = File(path)
    if (!file.exists()) return 0L

    // If it's a file (PDF, Image), return size immediately
    if (file.isFile) return file.length()

    // If it's a folder:
    return if (deepScan) {
        // Deep Scan: Walks all subdirectories (Slow, for background)
        calculateFolderSizeRecursive(file)
    } else {
        // Light Scan: Only counts files in the top level (Fast, for UI listing)
        calculateFolderSizeNonRecursive(file)
    }
}

private fun calculateFolderSizeRecursive(root: File): Long {
    // OPTIMIZATION: Do not auto-calculate the Android system folder.
    // This folder contains thousands of files and is restricted on Android 11+.
    // Calculating it is the main cause of slow startup.
    val path = root.absolutePath
    if (path.endsWith("/Android") || path.contains("/.thumbnails")
        || path.contains("/com.google.android.gms")) {
        return 0L
    }

    var total = 0L
    val stack = mutableListOf(root)

    try {
        while (stack.isNotEmpty()) {
            val current = stack.removeAt(stack.size - 1)

            // Get all children (files and folders)
            val children = current.listFiles() ?: continue

            for (child in children) {
                // Skip hidden files and the Android system folder COMPLETELY
                if (child.name.startsWith(".") || child.name == "Android") continue

                if (child.isFile) {
                    total += child.length()
                } else {
                    // Optimization: Don't go deeper than 10 levels for auto-calculation
                    if (stack.size < 100) {
                        stack.add(child)
                    }
                }
            }

        }
    } catch (e: Exception) {
        // Handle potential permission changes or deletions during calculation
        e.printStackTrace()
    }
    return total
}



private fun calculateFolderSizeNonRecursive(folder: File): Long {
    var total = 0L
    folder.listFiles()?.forEach { child ->
        if (!child.name.startsWith(".") && child.isFile) {
            total += child.length()
        }
    }
    return total
}






