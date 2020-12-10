package com.example.notes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Note::class, LocallyDeletedNoteId::class], version = 2)
@TypeConverters(Converters::class)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

}