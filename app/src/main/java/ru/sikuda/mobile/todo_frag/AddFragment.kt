package ru.sikuda.mobile.todo_frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.sikuda.mobile.todo_frag.databinding.FragmentAddBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import ru.sikuda.mobile.todo_frag.model.Note
import ru.sikuda.mobile.todo_frag.model.NoteDatabaseHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val viewModel: MainModel by viewModels()

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

            //val dateStr = binding.dateInput.text.toString()
            //val date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE)
            val date = LocalDateTime.now().toString()
            val content = binding.contextInput.text.toString()
            val detail = binding.detailInput.text.toString()
            //val note = Note(0, date, context, detail)
            //viewModel.insertNote(note)

            NoteDatabaseHelper(this.context).addNote(date,content, detail)
            findNavController().popBackStack();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
