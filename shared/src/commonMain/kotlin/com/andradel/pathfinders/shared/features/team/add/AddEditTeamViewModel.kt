package com.andradel.pathfinders.shared.features.team.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.team.TeamFirebaseDataSource
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.team.NewTeam
import com.andradel.pathfinders.shared.model.team.Team
import com.andradel.pathfinders.shared.validation.NameValidation
import com.andradel.pathfinders.shared.validation.isValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AddEditTeamViewModel(
    private val team: Team?,
    archiveName: String?,
    private val dataSource: TeamFirebaseDataSource,
    private val nameValidation: NameValidation,
) : ViewModel() {
    private val name = MutableStateFlow(team?.name.orEmpty())
    private val participants = MutableStateFlow(team?.participants.orEmpty())
    private val teamResult = MutableStateFlow<TeamResult?>(null)

    val isEditing = team?.id != null
    val isArchived = archiveName != null

    val state = combine(name, participants, teamResult) { name, participants, teamResult ->
        val nameValidation = nameValidation.validate(name)
        AddEditTeamState(
            name = name,
            nameValidation = nameValidation,
            participants = participants,
            isValid = nameValidation.isValid && !isArchived && teamResult == null,
            teamResult = teamResult,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEditTeamState())

    fun setName(value: String) {
        name.value = value
    }

    fun setSelection(participants: List<Participant>) {
        this.participants.value = participants
    }

    fun onSave() {
        teamResult.value = TeamResult.Loading
        viewModelScope.launch {
            dataSource.addOrUpdate(state.value.toNewTeam(), teamId = team?.id).onSuccess {
                teamResult.value = TeamResult.Success
            }.onFailure {
                teamResult.value = TeamResult.Failure
            }
        }
    }

    private fun AddEditTeamState.toNewTeam(): NewTeam {
        return NewTeam(name = name, participants = participants)
    }

    fun delete() {
        teamResult.value = TeamResult.Loading
        if (team?.id != null) {
            viewModelScope.launch {
                dataSource.delete(team.id).onSuccess {
                    teamResult.value = TeamResult.Success
                }.onFailure {
                    teamResult.value = TeamResult.Failure
                }
            }
        }
    }
}