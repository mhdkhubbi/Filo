package io.mhdkhubbi.filo.domain

data class FsEntry(
    val name: String,
    val fullPath: String,
    val isDirectory: Boolean,
    val type: FileType,
    val sizeBytes: Long,
    val itemCount: Int,
    val sizeMega:String
)

enum class FileType {
    FOLDER,
    PDF,
    APK,
    IMAGE,
    VIDEO,
    AUDIO,
    OTHER
}
data class StorageStats(
    val usedBytes: Long,
    val totalBytes: Long
)