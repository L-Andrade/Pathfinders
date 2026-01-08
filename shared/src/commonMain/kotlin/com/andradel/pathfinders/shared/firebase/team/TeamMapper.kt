package com.andradel.pathfinders.shared.firebase.team

import com.andradel.pathfinders.shared.firebase.participant.FirebaseParticipant
import com.andradel.pathfinders.shared.firebase.participant.ParticipantMapper
import com.andradel.pathfinders.shared.model.team.NewTeam
import com.andradel.pathfinders.shared.model.team.Team
import org.koin.core.annotation.Factory

@Factory
class TeamMapper(private val participantMapper: ParticipantMapper) {
    fun toTeams(
        teams: Map<String, FirebaseTeam>,
        participants: Map<String, FirebaseParticipant>,
        archiveName: String?,
    ): List<Team> {
        return teams.map { (key, value) -> toTeam(key, value, participants, archiveName) }
    }

    fun toTeam(
        teamId: String,
        value: FirebaseTeam,
        participants: Map<String, FirebaseParticipant>,
        archiveName: String?,
    ): Team = Team(
        teamId,
        value.name,
        value.participantIds.mapNotNull { id ->
            participants[id]?.let { participantMapper.toParticipant(id, it, archiveName) }
        },
    )

    fun toFirebaseTeam(team: NewTeam): FirebaseTeam {
        return FirebaseTeam(name = team.name, participantIds = team.participants.map { it.id })
    }
}