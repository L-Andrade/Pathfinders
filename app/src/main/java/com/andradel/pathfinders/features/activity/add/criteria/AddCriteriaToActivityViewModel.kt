package com.andradel.pathfinders.features.activity.add.criteria

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.activity.ActivityCriteriaFirebaseDataSource
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.criteria.CriteriaSelectionArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCriteriaToActivityViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataSource: ActivityCriteriaFirebaseDataSource
) : ViewModel() {
    private val selection = MutableStateFlow(handle.navArgs<CriteriaSelectionArg>().selection.toList())

    val state = combine(selection, dataSource.criteria) { selection, criteria ->
        AddCriteriaToActivityState.Loaded(
            selection = selection,
            criteria = criteria.filter { it !in selection }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddCriteriaToActivityState.Loading)

    fun addCriteria(name: String, maxScore: Int) {
        viewModelScope.launch {
            dataSource.addCriteria(name, maxScore)
        }
    }

    fun selectCriteria(criteria: ActivityCriteria) {
        selection.update { selection -> selection + criteria }
    }

    fun unselectCriteria(criteria: ActivityCriteria) {
        selection.update { selection -> selection - criteria }
    }
}