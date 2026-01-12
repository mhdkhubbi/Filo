package io.mhdkhubbi.filo.domain

data class FileEntry(
    val name: String,
    val path: String,
    val isFolder: Boolean,
    val type: FileType,
    val sizeBytes: Long,
    val childrenCount: Int,
    val formattedSize:String
)
enum class MediaType { IMAGES, VIDEOS, AUDIO, DOWNLOADS, DOCUMENTS }
enum class FileType {
    FOLDER,
    PDF,
    APK,
    IMAGE,
    VIDEO,
    AUDIO,
    OTHER
}
data class StorageVolume(
    val rootPath: String,
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long,
    val usagePercent: Int,
    val totalCapacity: String,
    val isExternal: Boolean,

    )

