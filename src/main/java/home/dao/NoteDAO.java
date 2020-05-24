package home.dao;

import home.model.*;

import java.util.List;

public interface NoteDAO {
    List<Note> allNotes(long userId);
    void add(Note note, long userId);
    void delete(Note note);
    void edit(Note note);
    Note getById(int id);
    List<NoteHistory> historicalNotes(int id);
    boolean exportToJson(int id);
    boolean importFromJson(String jsonString, long userId);
    void addPermissions(UserPermissions userPermission);
}
