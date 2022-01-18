package ru.sikuda.mobile.todo_frag

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.sikuda.mobile.todo_frag.databinding.FragmentUpdateBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import ru.sikuda.mobile.todo_frag.model.Note
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding
    private val model: MainModel by activityViewModels()
    private lateinit var note: Note
    private var index: Int = 0

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

        binding.dateInput2.setText(note.date)
        binding.contextInput2.setText(note.content)
        binding.detailInput2.setText(note.details)

        var date = LocalDate.parse(note.date)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            LocalDate.of(year,monthOfYear+1, dayOfMonth).also { date = it }
            binding.dateInput2.setText(date.format(formatter))
        }

        binding.dateButton.setOnClickListener {
            DatePickerDialog( this.requireContext(), dateSetListener,
                date.year, //cal.get(Calendar.YEAR),
                date.monthValue-1, //cal.get(Calendar.MONTH),
                date.dayOfMonth //cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.updateButton.setOnClickListener {

            val dating = binding.dateInput2.text.toString()
            val content = binding.contextInput2.text.toString()
            val detail = binding.detailInput2.text.toString()

            model.updateNote(index, note.id.toString(), dating, content, detail, note.fileimage)
            findNavController().popBackStack()
        }

        binding.deleteButton.setOnClickListener {
            model.deleteNote(index, note.id)
            findNavController().popBackStack()
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}
