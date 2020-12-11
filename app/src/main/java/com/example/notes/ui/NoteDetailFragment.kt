package com.example.notes.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.data.local.Note
import com.example.notes.databinding.FragmentNoteDetailBinding
import com.example.notes.dialogs.AddOwnerToNoteDialog
import com.example.notes.other.Status
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
        setHasOptionsMenu(true)
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null) {
            val addOwnerDialog = parentFragmentManager.findFragmentByTag("TAG_2") as AddOwnerToNoteDialog?
            addOwnerDialog?.setPositiveListener {
                addOwnerToCurrentNote(it)
            }
        }

        subscribeToObservers()

        binding.fabEditNote.setOnClickListener {
            findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id))
        }



    }



    private fun subscribeToObservers() {
        model.addOwnerStatus.observe(viewLifecycleOwner, { event ->
            event?.getContentIfNotHandled()?.let { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        binding.addOwnerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Successfully added")
                    }
                    Status.ERROR -> {
                        binding.addOwnerProgressBar.visibility = View.GONE
                        showSnackBar(result.message ?: "An unknown error occurred")
                    }
                    Status.LOADING -> {
                        binding.addOwnerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
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

    private fun showOwnerDialog() {
        AddOwnerToNoteDialog().apply {
            setPositiveListener {
                addOwnerToCurrentNote(it)
            }
        }.show(parentFragmentManager, "TAG_2")
    }

    private fun addOwnerToCurrentNote(email: String) {
        currentNote?.let {
            model.addOwnerToNote(it.id, email)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miAddOwner -> showOwnerDialog()
        }

        return super.onOptionsItemSelected(item)
    }

}