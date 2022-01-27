package ru.sikuda.mobile.todo_frag

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.sikuda.mobile.todo_frag.databinding.FragmentUpdateBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding
    private val model: MainModel by activityViewModels()
    private var latestUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val note = model.getNote(model.index)

        binding.dateInput.setText(note.date)
        binding.contextInput.setText(note.content)
        binding.detailInput.setText(note.details)
        if (model.tmpFile != null) {
            Glide.with(this)
                .load(model.tmpFile?.absolutePath)
                .into(binding.imageView)
        }else if (note.fileimage.isNotBlank()) {
            model.tmpFile = File(note.fileimage)
            Glide.with(this)
                .load(note.fileimage)
                .into(binding.imageView)
        }else binding.imageView.setImageResource(R.drawable.ic_photo)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        var date = LocalDateTime.parse(note.date, formatter)
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                date = date.withYear(year).withMonth(monthOfYear + 1).withDayOfMonth(dayOfMonth)
                binding.dateInput.setText(date.format(formatter))
            }
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hh, min ->
            date.withHour(hh).withMinute(min).also { date = it }
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
            DatePickerDialog(
                this.requireContext(), dateSetListener,
                date.year, //cal.get(Calendar.YEAR),
                date.monthValue - 1, //cal.get(Calendar.MONTH),
                date.dayOfMonth //cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.timeButton.setOnClickListener {
            TimePickerDialog(
                this.requireContext(), timeSetListener,
                date.hour,
                date.minute, //cal.get(Calendar.MONTH),
                true
            ).show()
        }

        binding.updateButton.setOnClickListener {

            val dating = binding.dateInput.text.toString()
            val content = binding.contextInput.text.toString()
            val detail = binding.detailInput.text.toString()

            var imagefilepath = note.fileimage
            if (model.tmpFile != null) {

                val filedir = NotesApp.appContext.getExternalFilesDir(null) //getDataDirectory()
                val imagefile = File(filedir, "${UUID.randomUUID()}.jpg")
                if (model.tmpFile?.copyTo(imagefile) == imagefile) {
                    imagefilepath = imagefile.absolutePath
                }
            }
            model.deleteTmpFile()
            model.updateNote(
                model.index,
                note.id.toString(),
                dating,
                content,
                detail,
                imagefilepath
            )
            findNavController().popBackStack()
        }

        binding.deleteButton.setOnClickListener {
            model.deleteNote(model.index, note.id)
            model.deleteTmpFile()
            findNavController().popBackStack()
        }
    }

    //take photo
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                model.getTmpFileUri().let { uri ->
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
        }
        // //else {
            // something was wrong
            // NotesApp.showToast(R.string.something_wrong)
        //}
    }

    private fun showSettingsDialog() {
        //DontAskAgainFragment().show(parentFragmentManager, DontAskAgainFragment.TAG)
        NotesApp.showToast(R.string.denied_toast)
    }



}
