package com.example.notes.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.data.local.Note
import com.example.notes.databinding.NoteItemBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(var notesList: List<Note>, private val listener: OnItemClickListener, private val context: Context): RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = notesList[position]

        holder.onNoteClicked(listener, note)

        holder.titleTextView.text = note.title

        if(!note.isSynced) {
            holder.syncedImage.setImageResource(R.drawable.ic_cross)
            holder.syncedTextView.text = "Not Synced"
        }
        else {
            holder.syncedImage.setImageResource(R.drawable.ic_check)
            holder.syncedTextView.text = "Synced"
        }

        val dateFormat = SimpleDateFormat("dd:MM:yy, HH:mm", Locale.getDefault())
        val date = dateFormat.format(note.date)

        holder.dateTextView.text = date

        val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#${note.color}")
            DrawableCompat.setTint(wrappedDrawable, color)
            holder.circle.background = it
        }


    }

    override fun getItemCount(): Int = notesList.size




    class MyViewHolder(itemView: NoteItemBinding): RecyclerView.ViewHolder(itemView.root) {

        val titleTextView = itemView.tvTitle
        val syncedImage = itemView.ivSynced
        val syncedTextView = itemView.tvSynced
        val dateTextView = itemView.tvDate
        val circle = itemView.viewNoteColor

        fun onNoteClicked(listener: OnItemClickListener, note: Note) {
            itemView.setOnClickListener {
                listener.onItemClicked(note)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClicked(note: Note)
    }
}