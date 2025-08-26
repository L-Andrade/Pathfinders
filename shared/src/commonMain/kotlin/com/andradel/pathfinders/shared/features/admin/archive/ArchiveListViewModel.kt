package com.andradel.pathfinders.shared.features.admin.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.archive.ArchiveFirebaseDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ArchiveListViewModel(private val dataSource: ArchiveFirebaseDataSource) : ViewModel() {
    val state = dataSource.archive.map { archives ->
        ArchiveListState.Archives(
            archives.map { archive ->
                val dates = archive.activities.mapNotNull { it.date }
                ArchiveItem(
                    name = archive.name,
                    participants = archive.participants.size,
                    activities = archive.activities.size,
                    startDate = dates.minOrNull()?.toString(),
                    endDate = dates.maxOrNull()?.toString(),
                )
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ArchiveListState.Loading)

    fun onDeleteArchive(name: String) {
        viewModelScope.launch {
            dataSource.deleteArchive(name)
        }
    }
}