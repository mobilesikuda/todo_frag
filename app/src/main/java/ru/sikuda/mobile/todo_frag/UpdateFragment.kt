package ru.sikuda.mobile.todo_frag

import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.sikuda.mobile.todo_frag.databinding.FragmentUpdateBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import ru.sikuda.mobile.todo_frag.model.Note
import ru.sikuda.mobile.todo_frag.model.NoteDatabaseHelper
import java.time.LocalDate
import java.util.*


class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val model: MainModel by activityViewModels()

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var note: Note

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

        val date = LocalDate.parse(note.date)
        //val cal = Calendar.getInstance()
        //cal.set(date.year, date.monthValue, date.dayOfMonth)

        //val sdf = SimpleDateFormat("yyyy.MM.dd", Locale("ru", "RU"))
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            //date(year,)
            //cal.set(Calendar.YEAR, year)
            //cal.set(Calendar.MONTH, monthOfYear)
            //cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.dateInput2.setText(year.toString()+"-"+monthOfYear.toString()+"-"+dayOfMonth.toString())
        }

        binding.dateInput2.setOnClickListener {
            DatePickerDialog( this.requireContext(), dateSetListener,
                date.year, //cal.get(Calendar.YEAR),
                date.monthValue, //cal.get(Calendar.MONTH),
                date.year //cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.updateButton.setOnClickListener {

            val date = binding.dateInput2.text.toString()
            val content = binding.contextInput2.text.toString()
            val detail = binding.detailInput2.text.toString()

            model.updateNote(note.id.toString(), date, content, detail)
            findNavController().popBackStack();
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
