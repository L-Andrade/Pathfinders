package com.andradel.pathfinders.shared.features.admin.archive.create

data class CreateArchiveProgressState(
    val createdArchive: ArchiveState,
    val deletedActivities: ArchiveState? = null,
    val deletedParticipants: ArchiveState? = null,
    val deletedCriteria: ArchiveState? = null,
    val finished: Boolean = false,
)

enum class ArchiveState {
    InProgress,
    Success,
    Fail,
}