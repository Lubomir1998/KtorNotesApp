package com.example.notes.viewmodels

import androidx.core.text.isDigitsOnly
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

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus: LiveData<Resource<String>> = _loginStatus

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
        if(password.length < 6 || password.length > 18) {
            _registerStatus.postValue(Resource.error("Password must be between 6 and 18 characters", null))
            return
        }
        if(password.isDigitsOnly()) {
            _registerStatus.postValue(Resource.error("Password must contain at least one letter", null))
            return
        }
        if(!(password.matches(".*\\d+.*".toRegex()))) {
            _registerStatus.postValue(Resource.error("Password must contain at least one number", null))
            return
        }
        viewModelScope.launch {
            val result = repository.registerUser(email, password)
            _registerStatus.postValue(result)
        }

    }

    fun loginUser(email: String, password: String) {
        _loginStatus.postValue(Resource.loading(null))
        if(email.isEmpty() || password.isEmpty()) {
            _loginStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _loginStatus.postValue(result)
        }

    }

}