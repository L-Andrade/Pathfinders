package com.andradel.pathfinders.shared.firebase.team

import com.andradel.pathfinders.shared.extensions.throwCancellation
import com.andradel.pathfinders.shared.firebase.archiveChild
import com.andradel.pathfinders.shared.firebase.participant.FirebaseParticipant
import com.andradel.pathfinders.shared.firebase.toFlow
import com.andradel.pathfinders.shared.firebase.toMapFlow
import com.andradel.pathfinders.shared.model.team.NewTeam
import com.andradel.pathfinders.shared.model.team.Team
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.Factory

@Factory
class TeamFirebaseDataSource(private val mapper: TeamMapper) {
    private val db = Firebase.database
    private fun teamsRef(archiveName: String?) = db.reference().archiveChild(archiveName, "teams")
    private fun participantsRef(archiveName: String?) = db.reference().archiveChild(archiveName, "participants")

    fun team(teamId: String, archiveName: String?): Flow<Team?> = combine(
        teamsRef(archiveName).child(teamId).toFlow<FirebaseTeam>(),
        participantsRef(archiveName).toMapFlow<FirebaseParticipant>(),
    ) { value, participants -> mapper.toTeam(value.first, value.second, participants, archiveName) }

    fun teams(archiveName: String?): Flow<List<Team>> = combine(
        teamsRef(archiveName).toMapFlow<FirebaseTeam>(),
        participantsRef(archiveName).toMapFlow<FirebaseParticipant>(),
    ) { teams, participants -> mapper.toTeams(teams, participants, archiveName) }

    suspend fun addOrUpdate(team: NewTeam, teamId: String?): Result<Unit> = runCatching {
        val ref = teamsRef(null)
        val key = teamId ?: requireNotNull(ref.push().key)
        val firebaseTeam = mapper.toFirebaseTeam(team)
        ref.child(key).setValue(firebaseTeam)
    }.throwCancellation()

    suspend fun delete(teamId: String) = runCatching {
        teamsRef(null).child(teamId).removeValue()
    }.throwCancellation()
}