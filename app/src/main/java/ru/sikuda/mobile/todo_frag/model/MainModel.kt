package ru.sikuda.mobile.todo_frag.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.sikuda.mobile.todo_frag.NotesApp


class MainModel() : ViewModel() {

    var _list: MutableLiveData<ArrayList<Note>> = MutableLiveData<ArrayList<Note>>()
    val list: LiveData<ArrayList<Note>>
        get() = _list
    private val myDB = NoteDatabaseHelper(NotesApp.appContext)

    init {
        _list.value = ArrayList<Note>()
        getAllNotes()
    }

    fun getNote(id: Int): Note {
        return _list.value?.get(id)!!
    }

    fun size(): Int{
        return _list.value!!.size
    }

    //ArrayList<Note>
    fun getAllNotes() {
        viewModelScope.launch {

            val notes: ArrayList<Note> = ArrayList<Note>()
            val cursor = myDB.readAllData()
            notes.clear()
            while (cursor!!.moveToNext()) {

                val date = cursor.getString(1)
                val note: Note = Note(cursor.getLong(0), date, cursor.getString(2), cursor.getString(3))
                notes.add(note)
            }
            _list.value = notes
            return@launch
        }
    }

    fun updateNote(index: Int, id: String, date: String, content: String, detail: String){

        _list.value?.set(index, Note(id.toLong(), date, content, detail))
        viewModelScope.launch {
            myDB.updateNote( id, date, content, detail)
        }
    }

    fun insertNote(date: String, content: String, detail: String){
        viewModelScope.launch {
            myDB.addNote(date, content, detail)
            getAllNotes()
        }
    }

    fun deleteNote(index: Int, id: Long){

        _list.value?.removeAt(index)
        viewModelScope.launch {
            myDB.deleteNote(id.toString())
            //getAllNotes()
        }
    }

    fun deleteAllNotes(){
        _list.value?.clear()
        viewModelScope.launch {
            myDB.deleteAllData()
        }
    }

}


