package com.andradel.pathfinders.features.activity.evaluate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.model.activity.ActivityArg
import com.andradel.pathfinders.model.participant.Participant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluateActivityViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataSource: ActivityFirebaseDataSource,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {
    val activity = handle.navArgs<ActivityArg>().activity

    private val scores = MutableStateFlow(activity.scores)
    val state = scores.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _result = Channel<Result<Unit>>()
    val result = _result.receiveAsFlow()

    val isUnsaved: Boolean
        get() = scores.value != activity.scores

    fun setScore(participant: Participant, criteriaId: String, score: Int) {
        scores.update { scores ->
            val participantScores =  scores[participant.id].orEmpty().toMutableMap()
            participantScores[criteriaId] = score
            scores.toMutableMap().apply { this[participant.id] = participantScores }
        }
    }

    fun updateActivityScores() {
        _loading.value = true
        coroutineScope.launch {
            _result.send(dataSource.updateScores(activityId = activity.id, scores = scores.value))
            _loading.value = false
        }
    }
}