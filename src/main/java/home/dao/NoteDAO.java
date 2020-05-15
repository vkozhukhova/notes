package home.dao;

import home.model.Note;

import java.util.List;

public interface NoteDAO {
    List<Note> allNotes();
    void add(Note note);
    void delete(Note note);
    void edit(Note note);
    Note getById(int id);
}
