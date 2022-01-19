package ru.sikuda.mobile.todo_frag

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class NotesApp: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set

        fun showToast(textId: Int) {
            Toast.makeText( appContext, textId, Toast.LENGTH_SHORT).show()
        }

        fun getTmpFileUri(): Uri {

            val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
                createNewFile()
                deleteOnExit()
            }

            return FileProvider.getUriForFile(appContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
        }

        fun getNewFile() {

        }
    }

}


