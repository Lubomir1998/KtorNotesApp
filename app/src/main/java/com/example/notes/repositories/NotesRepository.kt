package com.example.notes.repositories

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.notes.data.local.Note
import com.example.notes.data.local.NoteDao
import com.example.notes.data.remote.NoteApi
import com.example.notes.data.remote.requests.AccountRequest
import com.example.notes.networkBoundResource
import com.example.notes.other.Resource
import com.example.notes.other.checkForInternetConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepository
@Inject constructor(
        private val noteDao: NoteDao,
        private val noteApi: NoteApi,
        private val context: Application
){

    suspend fun registerUser(email: String, password: String) = withContext(Dispatchers.IO) {
        try{
            val response = noteApi.register(AccountRequest(email, password))
            if(response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception){
            Resource.error("Couldn't connect to servers. Check your internet connection", null)
        }
    }

    suspend fun loginUser(email: String, password: String) = withContext(Dispatchers.IO) {
        try{
            val response = noteApi.login(AccountRequest(email, password))
            if(response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception){
            Resource.error("Couldn't connect to servers. Check your internet connection", null)
        }
    }

    suspend fun getNoteById(id: String) = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note) {
        val response = try {
            noteApi.insertNote(note)
        } catch(e: Exception) {
            null
        }
        if(response != null && response.isSuccessful) {
            noteDao.insertNote(note.apply { isSynced = true })
        } else {
            noteDao.insertNote(note)
        }
    }

    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach { insertNote(it) }
    }

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
                query = {
                    noteDao.getAllNotes()
                },
                fetch = {
                    noteApi.getNotes()
                },
                savedFetchResult = { response ->
                    response.body()?.let { notes ->
                        insertNotes(notes)
                    }
                },
                shouldFetch = {
                    checkForInternetConnection(context)
                }
        )
    }









}