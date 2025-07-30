package com.andradel.pathfinders.shared.features.admin.archive

sealed interface ArchiveListState {
    data object Loading : ArchiveListState
    data class Archives(val archives: List<ArchiveItem>) : ArchiveListState
}

data class ArchiveItem(
    val name: String,
    val participants: Int,
    val activities: Int,
    val startDate: String?,
    val endDate: String?,
)