package ru.sikuda.mobile.todo_frag.model

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.sikuda.mobile.todo_frag.BuildConfig
import ru.sikuda.mobile.todo_frag.NotesApp
import java.io.File


class MainModel() : ViewModel() {

    var _list: MutableLiveData<ArrayList<Note>> = MutableLiveData<ArrayList<Note>>()
    val list: LiveData<ArrayList<Note>>
        get() = _list

    var tmpFile: File? = null
    var index: Int = -1
    //ateinit var note: Note

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

                val note: Note = Note( cursor.getLong(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4))
                notes.add(note)
            }
            _list.value = notes
            return@launch
        }
    }

    fun updateNote(index: Int, id: String, date: String, content: String, detail: String, imagefile: String){

        _list.value?.set(index, Note(id.toLong(), date, content, detail, imagefile))
        viewModelScope.launch {
            myDB.updateNote( id, date, content, detail, imagefile )
        }
    }

    fun insertNote(date: String, content: String, detail: String, imagefile: String){
        viewModelScope.launch {
            myDB.addNote(date, content, detail, imagefile )
            getAllNotes()
        }
    }

    fun deleteNote(index: Int, id: Long){

        _list.value?.removeAt(index)
        viewModelScope.launch {
            myDB.deleteNote(id.toString())
        }
    }

    fun deleteAllNotes(){
        _list.value?.clear()
        viewModelScope.launch {
            myDB.deleteAllData()
        }
    }

//    fun setNote(pos: Int, id: Long){
//        index = pos
//        val note2 = getNote(id.toInt())
//        note.copy(
//            id = note2.id,
//            date = note2.date,
//            content = note2.content,
//            details = note2.details,
//            fileimage = note2.fileimage
//        )
//    }

    fun deleteTmpFile(){
        tmpFile?.delete()
        tmpFile = null;
    }

    fun getTmpFileUri(): Uri {
        tmpFile?.delete()
        tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(NotesApp.appContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile!!)
    }

}


