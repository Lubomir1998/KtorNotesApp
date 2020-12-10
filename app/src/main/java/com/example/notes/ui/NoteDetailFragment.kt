package com.example.notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.data.local.Note
import com.example.notes.databinding.FragmentNoteDetailBinding
import com.example.notes.viewmodels.NoteDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class NoteDetailFragment: BaseFragment(R.layout.fragment_note_detail) {

    private lateinit var binding: FragmentNoteDetailBinding
    private val model: NoteDetailViewModel by viewModels()
    private val args: NoteDetailFragmentArgs by navArgs()

    private var currentNote: Note? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        binding.fabEditNote.setOnClickListener {
            findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id))
        }



    }



    private fun subscribeToObservers() {
        model.observeNote(args.id).observe(viewLifecycleOwner, {
            it?.let { note ->
                binding.tvNoteTitle.text = note.title
                setMarkDownText(note.content)
                currentNote = note
            } ?: showSnackBar("Note not found")

        })
    }

    private fun setMarkDownText(text: String) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(binding.tvNoteContent, markdown)
    }

}