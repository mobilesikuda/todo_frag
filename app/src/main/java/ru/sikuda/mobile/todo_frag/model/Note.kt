package ru.sikuda.mobile.todo_frag.model

import java.time.LocalDateTime

data class Note(
    val id: Long,
    val date: String,
    val content: String,
    val details: String)
{
        companion object {
            const val UNKNOWN_CREATED_AT = 0L
        }
}