package ru.sikuda.mobile.todo_frag

import android.Manifest
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.sikuda.mobile.todo_frag.databinding.FragmentAddBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import java.io.File
import java.util.*

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private var latestUri: Uri? = null
    private val model: MainModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener {

            //val date = LocalDate.now().toString()
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ru", "RU"))
            val date = sdf.format(cal.time)
            val content = binding.contextInput.text.toString()
            val detail = binding.detailInput.text.toString()
            var image = ""
            if( model.tmpFile != null ){
                val filedir = NotesApp.appContext.getExternalFilesDir(null) //getDataDirectory()
                val imagefile = File(filedir,"${UUID.randomUUID()}.jpg")
                if( model.tmpFile?.copyTo(imagefile) == imagefile ) {
                    image = imagefile.absolutePath
                    model.deleteTmpFile()
                }
            }
            model.insertNote(date, content, detail, image)
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

    //take photo
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                lifecycleScope.launchWhenStarted {
                    model.getTmpFileUri().let { uri ->
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
 //           NotesApp.showToast(R.string.something_wrong)
        }
    }

    private fun showSettingsDialog() {
        //DontAskAgainFragment().show(parentFragmentManager, DontAskAgainFragment.TAG)
        NotesApp.showToast(R.string.denied_toast)
    }

}
