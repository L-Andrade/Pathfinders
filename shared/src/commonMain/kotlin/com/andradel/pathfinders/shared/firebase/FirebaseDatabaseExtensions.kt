package com.andradel.pathfinders.shared.firebase

import dev.gitlive.firebase.database.DataSnapshot
import dev.gitlive.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

inline fun <reified T> DatabaseReference.toMapFlow(): Flow<Map<String, T>> = valueEvents.map { dataSnapshot ->
    dataSnapshot.toMap()
}

inline fun <reified T> DatabaseReference.toFlow(): Flow<Pair<String, T>> = valueEvents.map { dataSnapshot ->
    dataSnapshot.key.orEmpty() to dataSnapshot.value()
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> DataSnapshot.toMap(): Map<String, T> = children
    .associate { it.key to it.value<T>() }
    .filterKeys { it != null }
    .filterValues { it != null } as Map<String, T>

suspend inline fun <reified T> DatabaseReference.getMap(): Map<String, T> = valueEvents.first().toMap()

suspend fun DatabaseReference.exists(): Boolean = valueEvents.first().exists

suspend inline fun <reified T> DatabaseReference.getValue(): T = valueEvents.first().value()

fun DatabaseReference.archiveChild(archiveName: String?, child: String): DatabaseReference =
    if (archiveName != null) child("archive/$archiveName/$child") else child(child)