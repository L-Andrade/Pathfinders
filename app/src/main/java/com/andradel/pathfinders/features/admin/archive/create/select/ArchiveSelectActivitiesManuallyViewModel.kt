package com.andradel.pathfinders.features.admin.archive.create.select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.model.activity.ActivitySelectionArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveSelectActivitiesManuallyViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataSource: ActivityFirebaseDataSource,
) : ViewModel() {
    private val initialSelection = handle.navArgs<ActivitySelectionArg>().selection
    private val selection = MutableStateFlow(initialSelection.map { it.id }.toSet())

    private val _result = Channel<ActivitySelectionArg>()
    val result = _result.receiveAsFlow()

    val state = combine(selection, dataSource.activities(null)) { selection, activities ->
        ArchiveSelectActivitiesManuallyState.Selection(
            activities.map { activity ->
                SelectableActivity(
                    selected = activity.id in selection,
                    id = activity.id,
                    name = activity.name,
                    classes = activity.classes,
                    date = activity.date?.toString(),
                )
            },
            selected = selection.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ArchiveSelectActivitiesManuallyState.Loading)

    fun select(id: String) {
        selection.update { items -> if (id in items) items - id else items + id }
    }

    fun onSelectActivities() {
        viewModelScope.launch {
            val activities = dataSource.activities(null).first()
            _result.send(ActivitySelectionArg(ArrayList(activities.filter { it.id in selection.value })))
        }
    }
}