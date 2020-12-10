package com.example.notes.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.notes.data.local.NoteDatabase
import com.example.notes.data.remote.BasicAuthInterceptor
import com.example.notes.data.remote.NoteApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideNoteDB(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        NoteDatabase::class.java,
        "notesDB"
    )
            .fallbackToDestructiveMigration()
            .build()


    @Singleton
    @Provides
    fun provideNoteDao(db: NoteDatabase) = db.noteDao()


    @Singleton
    @Provides
    fun provideBaseInterceptor() = BasicAuthInterceptor()


    @Singleton
    @Provides
    fun provideNoteApi(interceptor: BasicAuthInterceptor): NoteApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://192.168.0.102:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NoteApi::class.java)
    }


    @Singleton
    @Provides
    fun provideEncryptedSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "encryptedSharedPrefsName",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


}