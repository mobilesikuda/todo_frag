package ru.sikuda.mobile.todo_frag

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.sikuda.mobile.todo_frag.databinding.FragmentAddBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import java.io.File
import java.io.IOException
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Files.copy
import java.util.*


class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private var latestUri: Uri? = null
    private var tmpFile: File? = null
    private val model: MainModel by activityViewModels()

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener {

            //val date = LocalDate.now().toString()
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("ru", "RU"))
            val date = sdf.format(cal.time)
            val content = binding.contextInput.text.toString()
            val detail = binding.detailInput.text.toString()
            var imagefilepath = "";
            if( tmpFile != null ){

                val filedir = NotesApp.appContext.getExternalFilesDir(null) //getDataDirectory()
                val imagefile = File(filedir,"${UUID.randomUUID()}.jpg")
                if( tmpFile?.copyTo(imagefile) == imagefile ) {
                    imagefilepath = imagefile.absolutePath
                }
            }
            model.insertNote(date, content, detail, imagefilepath)
            findNavController().popBackStack();
        }

        binding.imageView.setOnClickListener {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // we need to tell user why do we need permission
                NotesApp.showToast(R.string.need_permission)
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //take photo
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                lifecycleScope.launchWhenStarted {
                    getTmpFileUri().let { uri ->
                        latestUri = uri
                        takeImageResult.launch(latestUri)
                    }
                }
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // user denied permission and set Don't ask again.
                showSettingsDialog()
            }
            else -> {
                NotesApp.showToast(R.string.denied_toast)
            }
        }
    }

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestUri?.let { uri ->
                binding.imageView.setImageURI(uri)
            }
        } else {
            // something was wrong
            NotesApp.showToast(R.string.something_wrong)
        }
    }

    private fun showSettingsDialog() {
        //DontAskAgainFragment().show(parentFragmentManager, DontAskAgainFragment.TAG)
        NotesApp.showToast(R.string.denied_toast)
    }

    private fun getTmpFileUri(): Uri {

//        return NotesApp.getTmpFileUri()

        tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(NotesApp.appContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile!!)


//        try {
//            imagefile?.delete()
//
//            val filedir = NotesApp.appContext.getExternalFilesDir(null) //getDataDirectory()
//            imagefile = File(filedir,"${UUID.randomUUID()}.jpg")
//            if ( imagefile.createNewFile() ) NotesApp.showToast(R.string.hello_add_fragment)
//
//        }
//        catch (e: IOException) {
//            NotesApp.showToast( R.string.app_name)
//        }
//
//        return imagefile!!.toUri()

        //return FileProvider.getUriForFile(NotesApp.appContext, "${BuildConfig.APPLICATION_ID}.provider", imagefile!!)
    }
}
