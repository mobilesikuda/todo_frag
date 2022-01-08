package ru.sikuda.mobile.todo_frag.model

import kotlinx.coroutines.flow.Flow

/**
 * Repository with notes-related actions
 */
interface NotesRepository {

    suspend fun getNotes(): List<Note>

    suspend fun getNoteById(id: Long): Flow<Note?>

    suspend fun insertNote(note: Note)

    suspend fun updateNote(note: Note)

}