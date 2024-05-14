package com.andradel.pathfinders.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

inline fun <reified T> DatabaseReference.toMapFlow(): Flow<Map<String, T>> = callbackFlow {
    val type = T::class.java
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            trySend(dataSnapshot.toMap(type))
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    addValueEventListener(listener)
    awaitClose {
        removeEventListener(listener)
    }
}

inline fun <reified T> DatabaseReference.toFlow(): Flow<Pair<String, T>> = callbackFlow {
    val type = T::class.java
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            trySend(dataSnapshot.key.orEmpty() to dataSnapshot.getValue(type) as T)
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    addValueEventListener(listener)
    awaitClose {
        removeEventListener(listener)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> DataSnapshot.toMap(type: Class<T>): Map<String, T> = children
    .associate { it.key to it.getValue(type) }
    .filterKeys { it != null }
    .filterValues { it != null } as Map<String, T>

suspend inline fun <reified T> DatabaseReference.getMap(): Map<String, T> = suspendCancellableCoroutine { cont ->
    val type = T::class.java
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            cont.resume(dataSnapshot.toMap(type))
        }

        override fun onCancelled(error: DatabaseError) {
            cont.resumeWithException(error.toException())
        }
    }
    addListenerForSingleValueEvent(listener)
    cont.invokeOnCancellation {
        removeEventListener(listener)
    }
}

suspend fun DatabaseReference.exists(): Boolean = suspendCancellableCoroutine { cont ->
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            cont.resume(dataSnapshot.exists())
        }

        override fun onCancelled(error: DatabaseError) {
            cont.resumeWithException(error.toException())
        }
    }
    addListenerForSingleValueEvent(listener)
    cont.invokeOnCancellation {
        removeEventListener(listener)
    }
}

suspend inline fun <reified T> DatabaseReference.getValue(): T = suspendCancellableCoroutine { cont ->
    val type = T::class.java
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            cont.resume(dataSnapshot.getValue(type) as T)
        }

        override fun onCancelled(error: DatabaseError) {
            cont.resumeWithException(error.toException())
        }
    }
    addListenerForSingleValueEvent(listener)
    cont.invokeOnCancellation {
        removeEventListener(listener)
    }
}

suspend inline fun <reified T> DatabaseReference.getGenericValue(): T = suspendCancellableCoroutine { cont ->
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            cont.resume(dataSnapshot.value as T)
        }

        override fun onCancelled(error: DatabaseError) {
            cont.resumeWithException(error.toException())
        }
    }
    addListenerForSingleValueEvent(listener)
    cont.invokeOnCancellation {
        removeEventListener(listener)
    }
}