package com.example.notes.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.notes.other.Constants.KEY_EMAIL
import com.example.notes.other.Constants.NO_EMAIL
import com.example.notes.R
import com.example.notes.data.local.Note
import com.example.notes.databinding.FragmentAddEditNoteBinding
import com.example.notes.dialogs.ColorPickerDialog
import com.example.notes.other.Status
import com.example.notes.viewmodels.AddEditNoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNoteFragment: BaseFragment(R.layout.fragment_add_edit_note) {

    private lateinit var binding: FragmentAddEditNoteBinding
    private val model: AddEditNoteViewModel by viewModels()

    val args: AddEditNoteFragmentArgs by navArgs()
    private var currentColor = "FFA201"
    private var currentNote: Note? = null

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.id.isNotEmpty()) {
            model.getNoteById(args.id)
            subscribeToObservers()
        }

        if(savedInstanceState != null) {
            val colorPickerDialog = parentFragmentManager.findFragmentByTag("Tag") as ColorPickerDialog?
            colorPickerDialog?.setPositiveListener {
                changeNoteColor(it)
            }
        }

        binding.viewNoteColor.setOnClickListener {
            ColorPickerDialog().apply {
                setPositiveListener {
                    changeNoteColor(it)
                }
            }.show(parentFragmentManager, "Tag")
        }






    }






    private fun subscribeToObservers() {
        model.note.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let { result ->
                when(result.status){
                    Status.SUCCESS -> {
                        val note = result.data!!
                        currentNote = note

                        binding.etNoteTitle.setText(note.title)
                        binding.etNoteContent.setText(note.content)
                        changeNoteColor(note.color)
                    }
                    Status.ERROR -> {
                        showSnackBar(result.message ?: "Note not found")
                    }
                    Status.LOADING -> {
                        // NO OP //
                    }
                }
            }
        })
    }


    private fun changeNoteColor(stringColor: String) {
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#$stringColor")
            DrawableCompat.setTint(wrappedDrawable, color)
            binding.viewNoteColor.background = wrappedDrawable
            currentColor = stringColor
        }
    }


    override fun onPause() {
        super.onPause()

        saveNote()
    }

    private fun saveNote() {
        val authEmail = sharedPrefs.getString(KEY_EMAIL, NO_EMAIL) ?: NO_EMAIL

        val title = binding.etNoteTitle.text.toString()
        val content = binding.etNoteContent.text.toString()

        if(title.isEmpty() || content.isEmpty()) {
            return
        }

        val date = System.currentTimeMillis()

        val color = currentColor

        val owners = currentNote?.owners ?: listOf(authEmail)

        val id = currentNote?.id ?: UUID.randomUUID().toString()

        val note = Note(title, content, date, owners, color, id = id)

        model.insertNote(note)
    }



}