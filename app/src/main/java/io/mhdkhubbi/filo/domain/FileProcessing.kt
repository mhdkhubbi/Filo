package io.mhdkhubbi.filo.domain


import android.os.Environment
import android.os.StatFs
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
fun listFilesInLight(path: String, sizeCache: Map<String, Long>): List<FsEntry> {
    val root = Path(path)
    return root.listDirectoryEntries().map { entry ->
        val cachedSize = sizeCache[entry.toString()]
        FsEntry(
            name = entry.name,
            fullPath = entry.toString(),
            isDirectory = entry.isDirectory(),
            type = detectType(entry),
            sizeBytes = if (entry.isDirectory()) cachedSize ?: 0L else entry.toFile().length(),
            itemCount = getItemCount(entry)
        )
    }
}
fun listFilesIn(path: String): List<FsEntry> {
    val root = Path(path)

    return root.listDirectoryEntries().map { entry ->
        FsEntry(
            name = entry.name,
            fullPath = entry.toString(),
            isDirectory = entry.isDirectory(),
            type = detectType(entry),
            sizeBytes = getSize(entry),
            itemCount = getItemCount(entry)
        )

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
fun getSize(entry: Path): Long {
    return try {
        val file = entry.toFile()
        if (file.isDirectory) getFolderSize(entry) else file.length()
    } catch (_: Exception) {
        0L
    }
}

fun getItemCount(entry: Path): Int {
    return try {
        if (entry.isDirectory()) entry.listDirectoryEntries().size else 0
    } catch (_: Exception) {
        0
    }
}
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
        val file = path.toFile()
        if (!file.isDirectory) return 0L

        file.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
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