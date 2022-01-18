package ru.sikuda.mobile.todo_frag

import android.app.Application
import android.content.Context
import android.widget.Toast

class NotesApp: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set

        fun showToast(textId: Int) {
            Toast.makeText( appContext, textId, Toast.LENGTH_SHORT).show()
        }
    }

}


