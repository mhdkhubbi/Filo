
import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.annotation.RequiresApi
import io.mhdkhubbi.filo.domain.FileType
import io.mhdkhubbi.filo.domain.FsEntry
import io.mhdkhubbi.filo.domain.StorageStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes

// -------------------- LISTING --------------------

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun listFilesInLight(path: String, sizeCache: Map<String, Long>): List<FsEntry> {
    val root = Path.of(path)
    return Files.newDirectoryStream(root).use { stream ->
        stream.asSequence()
            .filter { !it.fileName.toString().startsWith(".") }
            .map { entry ->
                val full = entry.toString()
                val isDir = Files.isDirectory(entry)

                val sizeBytes = if (isDir) {
                    sizeCache[full] ?: 0L
                } else {
                    sizeCache[full] ?: 0L // defer actual size calculation
                }

                FsEntry(
                    name = entry.fileName.toString(),
                    fullPath = full,
                    isDirectory = isDir,
                    type = detectType(entry),
                    sizeBytes = sizeBytes,
                    itemCount = 0, // lazy, compute later
                    sizeMega = formatSize(sizeBytes)
                )
            }
            .toList()
    }
}

// -------------------- COPY / MOVE --------------------

fun copyFile(src: Path, dest: Path, buffer: ByteArray) {
    try {
        Files.createDirectories(dest.parent)
        Files.newInputStream(src).use { input ->
            Files.newOutputStream(dest).use { output ->
                var bytes = input.read(buffer)
                while (bytes >= 0) {
                    output.write(buffer, 0, bytes)
                    bytes = input.read(buffer)
                }
            }
        }
    } catch (_: Exception) { }
}

suspend fun copyDirectory(src: Path, dest: Path) = withContext(Dispatchers.IO) {
    val buffer = ByteArray(64 * 1024)
    Files.walk(src).use { stream ->
        stream.forEach { path ->
            val relative = src.relativize(path)
            val target = dest.resolve(relative)
            if (Files.isDirectory(path)) {
                Files.createDirectories(target)
            } else {
                copyFile(path, target, buffer)
            }
        }
    }
}

suspend fun copyFileWithProgress(
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
suspend fun copyDirectoryWithProgress(
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
suspend fun copyWithProgress(src: Path, dest: Path, onProgress: (Long, Long) -> Unit) {
    if (Files.isDirectory(src)) {
        copyDirectoryWithProgress(src, dest, onProgress)
    } else {
        copyFileWithProgress(src, dest, onProgress)
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
suspend fun moveWithProgress(src: Path, dest: Path, onProgress: (Long, Long) -> Unit) {
    try {
        Files.createDirectories(dest.parent)
        Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING)
        onProgress(1, 1) // instant complete
    } catch (_: Exception) {
        copyWithProgress(src, dest, onProgress)
        deleteFileOrDirectory(src)
    }
}

// -------------------- DELETE --------------------

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

// -------------------- HELPERS --------------------

fun isDirectorySafe(path: Path): Boolean {
    return try {
        Files.readAttributes(path, BasicFileAttributes::class.java).isDirectory
    } catch (_: Exception) {
        path.toFile().isDirectory
    }
}

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
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024
    return when {
        bytes >= gb -> String.format("%.2f GB", bytes / gb)
        bytes >= mb -> String.format("%.2f MB", bytes / mb)
        bytes >= kb -> String.format("%.2f KB", bytes / kb)
        else -> "$bytes B"
    }
}

fun getStorageStats(): StorageStats {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val availableBlocks = stat.availableBlocksLong
    val totalBytes = totalBlocks * blockSize
    val freeBytes = availableBlocks * blockSize
    val usedBytes = totalBytes - freeBytes
    return StorageStats(usedBytes, totalBytes)
}
fun getFolderSize(path: Path): Long {
    return try {
        Files.walk(path).use { stream ->
            stream.filter { Files.isRegularFile(it) }
                .mapToLong { Files.size(it) }
                .sum()
        }
    } catch (_: Exception) {
        0L
    }
}