package com.example.notes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocallyDeletedNoteId(
        @PrimaryKey(autoGenerate = false)
        val locallyDeletedNoteId: String
)
