package com.example.notes.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.notes.Constants
import com.example.notes.R
import com.example.notes.databinding.FragmentNotesBinding
import com.example.notes.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment: BaseFragment(R.layout.fragment_notes) {

    private lateinit var binding: FragmentNotesBinding
    private val model: NotesViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



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