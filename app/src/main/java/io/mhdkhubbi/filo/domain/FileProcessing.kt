package io.mhdkhubbi.filo.domain


// ------------------------------------------------------------
//  HIGH‑PERFORMANCE FILE PROCESSING MODULE
//  Fully optimized for Android file managers
// ------------------------------------------------------------

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name


fun listFilesInLight(
    path: String,
    sizeCache: Map<String, Long>
): List<FsEntry> {
    val root = Path(path)
    return root.listDirectoryEntries()
        .asSequence()
        .filter { !it.name.startsWith(".") }
        .map { entry ->
            val full = entry.toString()
            val isDir = entry.isDirectory()

            val sizeBytes = if (isDir) {
                sizeCache[full] ?: 0L
            } else {
                try {
                    Files.size(entry)
                } catch (_: Exception) {
                    0L
                }
            }

            FsEntry(
                name = entry.name,
                fullPath = full,
                isDirectory = isDir,
                type = detectType(entry),
                sizeBytes = sizeBytes,
                itemCount = getItemCount(entry),       // lazy load
                sizeMega = ""         // lazy format
            )
        }
        .toList()
}


suspend fun copyFile(src: Path, dest: Path) = withContext(Dispatchers.IO) {
    try {
        Files.createDirectories(dest.parent)

        val buffer = ByteArray(64 * 1024) // 64 KB shared buffer

        Files.newInputStream(src).use { input ->
            Files.newOutputStream(dest).use { output ->
                var bytes = input.read(buffer)
                while (bytes >= 0) {
                    output.write(buffer, 0, bytes)
                    bytes = input.read(buffer)
                }
            }
        }

    } catch (_: Exception) {
    }
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
                copyFileFast(path, target, buffer)
            }
        }
    }
}

private fun copyFileFast(src: Path, dest: Path, buffer: ByteArray) {
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
    } catch (_: Exception) {
    }
}

suspend fun copyFileOrDirectory(src: Path, dest: Path) {
    if (isDirectorySafe(src)) {
        copyDirectory(src, dest)
    } else {
        copyFile(src, dest)
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

    val allFiles = Files.walk(src)
        .filter { Files.isRegularFile(it) }
        .toList()

    val totalBytes = allFiles.sumOf { Files.size(it) }
    var copiedBytes = 0L

    val buffer = ByteArray(64 * 1024)

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
suspend fun copyWithProgress(
    src: Path,
    dest: Path,
    onProgress: (Long, Long) -> Unit
) {
    if (Files.isDirectory(src)) {
        copyDirectoryWithProgress(src, dest, onProgress)
    } else {
        copyFileWithProgress(src, dest, onProgress)
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
suspend fun moveWithProgress(
    src: Path,
    dest: Path,
    onProgress: (Long, Long) -> Unit
) {
    try {
        // Try fast native move
        Files.move(src, dest)
        onProgress(1, 1) // instantly complete
    } catch (_: Exception) {
        // Fallback: copy with progress, then delete
        copyWithProgress(src, dest, onProgress)
        deleteFileOrDirectory(src)
    }
}

suspend fun deleteFileOrDirectory(path: Path) = withContext(Dispatchers.IO) {
    if (Files.notExists(path)) return@withContext

    Files.walk(path)
        .sorted(Comparator.reverseOrder())
        .forEach { Files.deleteIfExists(it) }
}


suspend fun moveFileOrDirectory(src: Path, dest: Path) = withContext(Dispatchers.IO) {
    try {
        Files.move(src, dest)
    } catch (_: Exception) {
        copyFileOrDirectory(src, dest)
        deleteFileOrDirectory(src)
    }
}


fun isDirectorySafe(path: Path): Boolean {
    return try {
        val attrs = Files.readAttributes(path, BasicFileAttributes::class.java)
        attrs.isDirectory
    } catch (_: Exception) {
        path.toFile().isDirectory
    }
}

fun detectType(entry: Path): FileType {
    val name = entry.name.lowercase()

    return when {
        entry.isDirectory() -> FileType.FOLDER
        name.endsWith(".pdf") -> FileType.PDF
        name.endsWith(".apk") -> FileType.APK
        name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".gif") -> FileType.IMAGE

        name.endsWith(".mp4") || name.endsWith(".mkv") ||
                name.endsWith(".avi") -> FileType.VIDEO

        name.endsWith(".mp3") || name.endsWith(".wav") ||
                name.endsWith(".aac") -> FileType.AUDIO

        else -> FileType.OTHER
    }
}


fun getItemCount(entry: Path): Int {
    return try {
        if (entry.isDirectory()) entry.listDirectoryEntries().size else 0
    } catch (_: Exception) {
        0
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


fun getFolderSize(path: Path): Long {
    return try {
        Files.walk(path)
            .filter { Files.isRegularFile(it) }
            .mapToLong { Files.size(it) }
            .sum()
    } catch (_: Exception) {
        0L
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