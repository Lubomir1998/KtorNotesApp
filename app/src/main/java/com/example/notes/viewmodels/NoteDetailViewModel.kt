package com.example.notes.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.other.Event
import com.example.notes.other.Resource
import com.example.notes.repositories.NotesRepository
import kotlinx.coroutines.launch

class NoteDetailViewModel @ViewModelInject constructor(private val repository: NotesRepository): ViewModel() {

    private val _addOwnerStatus = MutableLiveData<Event<Resource<String>>>()
    val addOwnerStatus: LiveData<Event<Resource<String>>> = _addOwnerStatus

    fun addOwnerToNote(noteId: String, owner: String) {
        _addOwnerStatus.postValue(Event(Resource.loading(null)))
        if(noteId.isEmpty() || owner.isEmpty()) {
            _addOwnerStatus.postValue(Event(Resource.error("Email can't be empty", null)))
            return
        }
        viewModelScope.launch {
            val result = repository.addOwnerToNote(noteId, owner)
            _addOwnerStatus.postValue(Event(result))
        }
    }

    fun observeNote(id: String) = repository.observeNote(id)

}