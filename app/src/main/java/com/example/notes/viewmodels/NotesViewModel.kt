package com.example.notes.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.notes.data.local.Note
import com.example.notes.other.Event
import com.example.notes.other.Resource
import com.example.notes.repositories.NotesRepository

class NotesViewModel @ViewModelInject constructor(private val repository: NotesRepository): ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _allNotes = _forceUpdate.switchMap {
        repository.getAllNotes().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val allNotes: LiveData<Event<Resource<List<Note>>>> = _allNotes

    fun syncAllNotes() = _forceUpdate.postValue(true)

}