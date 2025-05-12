package com.andradel.pathfinders.extensions

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun LocalDateTime.toMillis(): Long {
    return atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
}