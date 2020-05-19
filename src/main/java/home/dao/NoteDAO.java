package home.dao;

import home.model.Note;
import home.model.NoteHistory;

import java.util.List;

public interface NoteDAO {
    List<Note> allNotes();
    void add(Note note);
    void delete(Note note);
    void edit(Note note);
    Note getById(int id);
    List<NoteHistory> historicalNotes(int id);
}
