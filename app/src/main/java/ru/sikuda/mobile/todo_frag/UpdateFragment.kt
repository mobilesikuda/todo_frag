package ru.sikuda.mobile.todo_frag

import android.app.DatePickerDialog
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

    private var _binding: FragmentUpdateBinding? = null
    private val model: MainModel by activityViewModels()
    private lateinit var note: Note
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val id = arguments?.getLong("id") as Long
        val date = arguments?.getString("date_txt") as String
        val content = arguments?.getString("content") as String
        val details = arguments?.getString("details") as String
        note = Note(id,date, content, details)

        _binding = FragmentUpdateBinding.inflate(layoutInflater)
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
            LocalDate.of(year,monthOfYear, dayOfMonth).also { date = it }
            binding.dateInput2.setText(date.format(formatter))
        }

        binding.dateButton.setOnClickListener {
            DatePickerDialog( this.requireContext(), dateSetListener,
                date.year, //cal.get(Calendar.YEAR),
                date.monthValue, //cal.get(Calendar.MONTH),
                date.dayOfMonth //cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.updateButton.setOnClickListener {

            val dating = binding.dateInput2.text.toString()
            val content = binding.contextInput2.text.toString()
            val detail = binding.detailInput2.text.toString()

            model.updateNote(note.id.toString(), dating, content, detail)
            findNavController().popBackStack()
        }

        binding.deleteButton.setOnClickListener {
            model.deleteNote(note.id.toString())
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
