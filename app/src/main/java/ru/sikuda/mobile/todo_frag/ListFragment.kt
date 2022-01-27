package ru.sikuda.mobile.todo_frag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.sikuda.mobile.todo_frag.databinding.FragmentListBinding
import ru.sikuda.mobile.todo_frag.model.MainModel

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val model: MainModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        model.list.observe(viewLifecycleOwner) {
            // Update the UI
            showNoData()
        }

        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding.recyclerView.adapter = CustomAdapter(this.context, model)
            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_ListFragment_to_AddFragment)
        }
    }

    fun showNoData() {

        if (model.size() == 0) {
            binding.emptyImageview.visibility = View.VISIBLE
            binding.noData.visibility = View.VISIBLE
        } else {
            binding.emptyImageview.visibility = View.GONE
            binding.noData.visibility = View.GONE
        }
    }
}

class CustomAdapter(
    private val context: Context?,
    private val model: MainModel
) : RecyclerView.Adapter<CustomAdapter.MyNoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNoteHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.my_row, parent, false)
        return MyNoteHolder(view)
    }

    override fun onBindViewHolder(holder: MyNoteHolder, position: Int) {

        val note = model.getNote(position)

        holder.id = note.id
        holder.pos.text = (position+1).toString()
        holder.note_date.text = note.date
        holder.note_content.text = note.content
        if (note.fileimage.isNotBlank()) {
            Glide.with(NotesApp.appContext)
                .load(note.fileimage)
                .circleCrop()
                .into(holder.note_imagefile)
        }

        //Animation RecycleView
        holder.mainLayout.animation = AnimationUtils.loadAnimation(
            context, R.anim.translate_anim
        )

        //Recyclerview onClickListener
        holder.mainLayout.setOnClickListener {

            //val date = holder.note_date.text.toString()
            //val content = holder.note_content.text.toString()

            model.index = holder.adapterPosition
            it.findNavController().navigate(R.id.action_ListFragment_to_UpdateFragment)

        }
    }

    override fun getItemCount(): Int {
        return model.size()
    }

    class MyNoteHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var id: Long = 0

        var pos: TextView = itemView.findViewById(R.id.pos)
        var note_date: TextView = itemView.findViewById(R.id.note_date)
        var note_content: TextView = itemView.findViewById(R.id.note_content)
        var note_imagefile: ImageView = itemView.findViewById(R.id.imageView)

        //var note_details: TextView = itemView.findViewById(R.id.note_details)
        var mainLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)

    }
}