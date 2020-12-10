package com.example.notes.ui

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.Constants
import com.example.notes.R
import com.example.notes.adapters.NoteAdapter
import com.example.notes.data.local.Note
import com.example.notes.databinding.FragmentNotesBinding
import com.example.notes.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.notes.other.Status
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment: BaseFragment(R.layout.fragment_notes) {

    private val TAG = "NotesFragment"

    private lateinit var binding: FragmentNotesBinding
    private val model: NotesViewModel by viewModels()
    private var notes = listOf<Note>()
    private lateinit var listener: NoteAdapter.OnItemClickListener
    private lateinit var noteAdapter: NoteAdapter

    @Inject
    lateinit var sharedPrefs: SharedPreferences



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER

        listener = object : NoteAdapter.OnItemClickListener {
            override fun onItemClicked(note: Note) {
                val action = NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(note.id)
                findNavController().navigate(action)
            }
        }

        noteAdapter = NoteAdapter(listener, requireContext())

        setUpRecyclerView()
        subscribeToObservers()


        binding.addNoteBtn.setOnClickListener {
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(""))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            subscribeToObservers()
        }


    }










    private fun subscribeToObservers() {
        model.allNotes.observe(viewLifecycleOwner, {
            Log.d(TAG, "**********subscribeToObservers: it is null - ${it == null}")
            it?.let { event ->
                val result = event.peekContent()

                when (result.status) {
                    Status.SUCCESS -> {
                        displayData(result.data!!)
                        binding.swipeRefreshLayout.isRefreshing = false
                        Log.d(TAG, "*********subscribeToObservers: status success")
                    }

                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { error ->
                            error.message?.let { message ->
                                showSnackBar(message)
                            }
                        }
                        result.data?.let { notes ->
                            displayData(notes)
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                        Log.d(TAG, "*********subscribeToObservers: status error")
                    }

                    Status.LOADING -> {
                        result.data?.let { notes ->
                            displayData(notes)
                        }
                        binding.swipeRefreshLayout.isRefreshing = true
                        Log.d(TAG, "*********subscribeToObservers: status loading")
                    }
                }

            }
        })
    }


    private fun displayData(notes: List<Note>) {
        noteAdapter.notes = notes
        noteAdapter.notifyDataSetChanged()
    }

    private fun setUpRecyclerView() {
        binding.notesRecyclerView.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }



    private fun logOut() {
        sharedPrefs.edit()
                .putString(Constants.KEY_EMAIL, Constants.NO_EMAIL)
                .putString(Constants.KEY_PASSWORD, Constants.NO_PASSWORD)
                .apply()

        val navOptions = NavOptions.Builder().setPopUpTo(R.id.notesFragment, true).build()

        findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAuthFragment(), navOptions)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miLogout -> logOut()
        }

        return super.onOptionsItemSelected(item)
    }

}