package ru.sikuda.mobile.todo_frag

import android.Manifest
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.sikuda.mobile.todo_frag.databinding.FragmentUpdateBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import ru.sikuda.mobile.todo_frag.model.Note
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding
    private val model: MainModel by activityViewModels()
    private lateinit var note: Note
    private var index: Int = 0

    private var latestUri: Uri? = null
    private var tmpFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        index = arguments?.getInt("index") as Int
        val id = arguments?.getLong("id") as Long
        val date = arguments?.getString("date_txt") as String
        val content = arguments?.getString("content") as String
        val details = arguments?.getString("details") as String
        val fileimage = arguments?.getString("fileimage") as String
        note = Note(id,date, content, details, fileimage)

        binding = FragmentUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dateInput.setText(note.date)
        binding.contextInput.setText(note.content)
        binding.detailInput.setText(note.details)
        if (note.fileimage.isNotBlank()) {
            Glide.with(this)
                .load(note.fileimage)
                .into(binding.imageView)
        }
        else binding.imageView.setImageResource(R.drawable.ic_photo)

        var date = LocalDate.parse(note.date)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            LocalDate.of(year,monthOfYear+1, dayOfMonth).also { date = it }
            binding.dateInput.setText(date.format(formatter))
        }

        binding.imageView.setOnClickListener {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // we need to tell user why do we need permission
                NotesApp.showToast(R.string.need_permission)
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.dateButton.setOnClickListener {
            DatePickerDialog( this.requireContext(), dateSetListener,
                date.year, //cal.get(Calendar.YEAR),
                date.monthValue-1, //cal.get(Calendar.MONTH),
                date.dayOfMonth //cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.updateButton.setOnClickListener {

            val dating = binding.dateInput.text.toString()
            val content = binding.contextInput.text.toString()
            val detail = binding.detailInput.text.toString()

            var imagefilepath = note.fileimage;
            if( tmpFile != null ){

                val filedir = NotesApp.appContext.getExternalFilesDir(null) //getDataDirectory()
                val imagefile = File(filedir,"${UUID.randomUUID()}.jpg")
                if( tmpFile?.copyTo(imagefile) == imagefile ) {
                    imagefilepath = imagefile.absolutePath
                }
            }
            model.updateNote(index, note.id.toString(), dating, content, detail, imagefilepath)
            findNavController().popBackStack()
        }

        binding.deleteButton.setOnClickListener {
            model.deleteNote(index, note.id)
            findNavController().popBackStack()
        }
    }

    //take photo
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                getTmpFileUri().let { uri ->
                    latestUri = uri
                    takeImageResult.launch(latestUri)
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

        tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(NotesApp.appContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile!!)
    }

}
