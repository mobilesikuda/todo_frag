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

    fun getAllNotes(): ArrayList<Note>{
 //       viewModelScope.launch {

            var notes: ArrayList<Note> = ArrayList<Note>()
            val cursor = myDB.readAllData()
            if (cursor?.count == 0) {
                //binding.emptyImageview.visibility = View.VISIBLE
                //binding.noData.visibility = View.VISIBLE
            } else {
                notes.clear()
                while (cursor!!.moveToNext()) {

                    val date = cursor.getString(1)
                    val note: Note = Note(cursor.getLong(0), date, cursor.getString(2), cursor.getString(3))
                    notes.add(note)
                }
            }
            _list.value = notes
            return notes
 //       }
    }

    fun updateNote(id: String, date: String, content: String, detail: String){
        viewModelScope.launch {
            myDB.updateNote( id, date, content, detail)
            //getAllNotes()
        }
    }

    fun insertNote(date: String, content: String, detail: String){
        viewModelScope.launch {
            myDB.addNote(date, content, detail)
            //getAllNotes()
        }
    }

    fun deleteNote(id: String){
        viewModelScope.launch {
            myDB.deleteNote(id)
            //getAllNotes()
        }
    }

    fun deleteAllNotes(){
        myDB.deleteAllData()
        _list.value = ArrayList<Note>()
    }

}


