package com.andradel.pathfinders.shared.features.admin.archive.create.select

import com.andradel.pathfinders.flavors.model.ParticipantClass

sealed interface ArchiveSelectActivitiesManuallyState {
    data object Loading : ArchiveSelectActivitiesManuallyState
    data class Selection(
        val activities: List<SelectableActivity>, val selected: Int,
    ) : ArchiveSelectActivitiesManuallyState
}

data class SelectableActivity(
    val selected: Boolean,
    val id: String,
    val name: String,
    val classes: List<ParticipantClass>,
    val date: String?,
)