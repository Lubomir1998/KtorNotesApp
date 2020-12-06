package com.example.notes.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.other.Resource
import com.example.notes.repositories.NotesRepository
import kotlinx.coroutines.launch

class AuthViewModel
@ViewModelInject constructor(private val repository: NotesRepository): ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<String>>()

    val registerStatus: LiveData<Resource<String>> = _registerStatus

    fun registerUser(email: String, password: String, confirmedPassword: String) {
        _registerStatus.postValue(Resource.loading(null))
        if(email.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
            _registerStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }
        if(password != confirmedPassword){
            _registerStatus.postValue(Resource.error("The passwords do not match", null))
            return
        }
        viewModelScope.launch {
            val result = repository.registerUser(email, password)
            _registerStatus.postValue(result)
        }

    }

}