package com.example.notes.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.notes.repositories.NotesRepository

class NoteDetailViewModel @ViewModelInject constructor(private val repository: NotesRepository): ViewModel() {

    fun observeNote(id: String) = repository.obserNote(id)

}