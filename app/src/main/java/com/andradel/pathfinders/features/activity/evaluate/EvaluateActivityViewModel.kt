package com.andradel.pathfinders.features.activity.evaluate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.model.activity.ActivityArg
import com.andradel.pathfinders.model.participant.Participant
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EvaluateActivityViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataSource: ActivityFirebaseDataSource,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {
    val activity = handle.navArgs<ActivityArg>().activity

    private val scores = MutableStateFlow(activity.scores)
    val state = scores.asStateFlow()

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
        coroutineScope.launch {
            dataSource.updateScores(activityId = activity.id, scores = scores.value)
        }
    }
}