package ru.sikuda.mobile.todo_frag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sikuda.mobile.todo_frag.databinding.FragmentListBinding
import ru.sikuda.mobile.todo_frag.model.MainModel
import ru.sikuda.mobile.todo_frag.model.Note
import ru.sikuda.mobile.todo_frag.model.NoteDatabaseHelper
import java.time.LocalDateTime
import java.util.ArrayList

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var notes: ArrayList<Note> = ArrayList<Note>()
    private val model: MainModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        model.list.observe(viewLifecycleOwner, {
            // Update the UI
            //notes = it as ArrayList<Note>
            showNoData()
        })

        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            notes = model.getAllNotes()
            binding.recyclerView.adapter = CustomAdapter(this.context, notes)
            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_ListFragment_to_AddFragment)
        }
    }

    fun showNoData() {

        if (notes.count() == 0) {
            binding.emptyImageview.visibility = View.VISIBLE
            binding.noData.visibility = View.VISIBLE
        } else {
            binding.emptyImageview.visibility = View.GONE
            binding.noData.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CustomAdapter(
    private val context: Context?,
    private val books: ArrayList<Note>
) : RecyclerView.Adapter<CustomAdapter.MyNoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNoteHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.my_row, parent, false)
        return MyNoteHolder(view)
    }

    override fun onBindViewHolder(holder: MyNoteHolder, position: Int) {
        holder.noteid.text = books[position].id.toString()
        holder.note_date.text = books[position].date.toString()
        holder.note_content.text = books[position].content.toString()
        holder.note_details.text = books[position].details.toString()

        //Animation RecycleView
        holder.mainLayout.animation = AnimationUtils.loadAnimation(
            context, R.anim.translate_anim
        )

        //Recyclerview onClickListener
        holder.mainLayout.setOnClickListener {

            val id = holder.noteid.text.toString().toLong()
            val date = holder.note_date.text.toString()
            val content = holder.note_content.text.toString()
            val details = holder.note_details.text.toString()

            val bundle = bundleOf("id" to id, "date_txt" to date, "content" to content, "details" to details)
            it.findNavController().navigate(R.id.action_ListFragment_to_UpdateFragment, bundle)

        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    class MyNoteHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var noteid: TextView = itemView.findViewById(R.id.note_id)
        var note_date: TextView = itemView.findViewById(R.id.note_date)
        var note_content: TextView = itemView.findViewById(R.id.note_content)
        var note_details: TextView = itemView.findViewById(R.id.note_details)
        var mainLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)

    }
}