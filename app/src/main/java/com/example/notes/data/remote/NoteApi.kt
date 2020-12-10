package com.example.notes.data.remote

import com.example.notes.data.local.Note
import com.example.notes.data.remote.requests.AccountRequest
import com.example.notes.data.remote.requests.AddOwnerRequest
import com.example.notes.data.remote.requests.DeleteNoteRequest
import com.example.notes.data.remote.responses.SimpleResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NoteApi {

    @POST("/register")
    suspend fun register(@Body request: AccountRequest): Response<SimpleResponse>

    @POST("/login")
    suspend fun login(@Body request: AccountRequest): Response<SimpleResponse>

    @POST("/saveNote")
    suspend fun insertNote(@Body note: Note): Response<ResponseBody>

    @POST("/addOwnerToNote")
    suspend fun addOwnerToNote(@Body request: AddOwnerRequest): Response<SimpleResponse>

    @POST("/deleteNote")
    suspend fun deleteNote(@Body request: DeleteNoteRequest): Response<ResponseBody>

    @GET("/getNotes")
    suspend fun getNotes(): Response<List<Note>>
}