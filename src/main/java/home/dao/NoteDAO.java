package home.dao;

import home.model.*;

import java.util.List;
import java.util.Map;

public interface NoteDAO {
    List<Note> allNotes(long userId);
    void add(Note note, long userId);
    void delete(Note note);
    void edit(Note note);
    Note getById(int id);
    List<NoteHistory> historicalNotes(int id);
    String exportToJson(int id);
    boolean importFromJson(String jsonString, long userId);
    String exportToXml(int id);
    boolean importFromXml(String xmlString, long userId);
    void addPermissions(UserPermissions userPermission);
    Map<Note, Permission> getOtherNotes(long userId);
    List<User> getViewUsersList(int id);
    List<User> getEditUsersList(int id);
}
