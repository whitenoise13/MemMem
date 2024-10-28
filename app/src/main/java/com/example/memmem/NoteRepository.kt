package com.example.memmem

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class NoteRepository(private val context: Context) {

    private val gson = Gson()
    private val fileName = "notes.json"

    fun getNotes(): MutableList<Note> {
        val file = File(context.filesDir, fileName)
        if(!file.exists()) {
            return mutableListOf()
        }
        val json = file.readText()
        val type = object : TypeToken<MutableList<Note>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveNote(notes: MutableList<Note>) {
        val file = File(context.filesDir, fileName)
        val json = gson.toJson(notes)
        file.writeText(json)
    }

    fun addNote(note: Note) {
        val notes = getNotes()
        notes.add(note)
        saveNote(notes)
    }

    fun updateNote(updateNote: Note) {
        val notes = getNotes()
        val index = notes.indexOfFirst { it.id == updateNote.id}
        if (index != -1) {
            notes[index] = updateNote
            saveNote(notes)
        }
    }

    fun deleteNote(noteId: Long) {
        val notes = getNotes()
        val updateNotes = notes.filter { it.id != noteId }.toMutableList()
        saveNote(updateNotes)
    }



}