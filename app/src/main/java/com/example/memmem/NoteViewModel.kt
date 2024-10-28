package com.example.memmem

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class NoteViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> get() = _notes

    fun addNote(title: String, content: String) {
        val newId = (_notes.maxByOrNull { it.id }?.id ?: 0) + 1 // 가장 큰 ID를 찾아서 +1
        _notes.add(Note(newId, title, content))
    }

    fun editNote(id: Long, newNote: Note) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            _notes[index] = newNote
        }
    }

    fun deleteNote(id: Long) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            _notes.removeAt(index)
        }
    }
}
