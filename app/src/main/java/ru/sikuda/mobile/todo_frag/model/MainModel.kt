package ru.sikuda.mobile.todo_frag.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class MainModel(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _list = MutableLiveData<List<Note>>()
    val list: LiveData<List<Note>>
        get() = _list

    init {
        viewModelScope.launch {
            // listening for the current account and send the username to be displayed in the toolbar
            _list.value = notesRepository.getNotes()
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch {
            //notesRepository.updateNote()
        }
    }

    fun insertNote(note: Note){
        viewModelScope.launch {
            notesRepository.insertNote(note)
        }
    }
}


