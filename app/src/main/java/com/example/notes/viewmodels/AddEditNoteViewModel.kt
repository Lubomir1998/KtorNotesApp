package com.example.notes.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.local.Note
import com.example.notes.other.Event
import com.example.notes.other.Resource
import com.example.notes.repositories.NotesRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddEditNoteViewModel @ViewModelInject constructor(private val repository: NotesRepository): ViewModel() {

    private val _notes = MutableLiveData<Event<Resource<Note>>>()
    val note: LiveData<Event<Resource<Note>>> = _notes

    fun insertNote(note: Note) = GlobalScope.launch {
        repository.insertNote(note)
    }

    fun getNoteById(noteId: String) = viewModelScope.launch {
        _notes.postValue(Event(Resource.loading(null)))
        val note = repository.getNoteById(noteId)

        note?.let {
            _notes.postValue(Event(Resource.success(it)))
        } ?: _notes.postValue(Event(Resource.error("Note not found", null)))
    }


}