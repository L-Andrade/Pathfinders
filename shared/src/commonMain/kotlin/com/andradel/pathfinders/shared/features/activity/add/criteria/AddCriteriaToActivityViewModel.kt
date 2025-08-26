package com.andradel.pathfinders.shared.features.activity.add.criteria

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.shared.firebase.activity.ActivityCriteriaFirebaseDataSource
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.customNavType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.reflect.typeOf

@KoinViewModel
class AddCriteriaToActivityViewModel(
    handle: SavedStateHandle,
    private val dataSource: ActivityCriteriaFirebaseDataSource,
) : ViewModel() {
    private val selection = MutableStateFlow(
        handle.toRoute<NavigationRoute.AddCriteriaToActivity>(
            typeMap = mapOf(typeOf<SelectedCriteria>() to customNavType<SelectedCriteria>()),
        ).selected.selection,
    )

    val state = combine(selection, dataSource.criteria) { selection, criteria ->
        AddCriteriaToActivityState.Loaded(
            selection = selection,
            criteria = criteria.filter { it !in selection },
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