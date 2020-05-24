package home.service;

import home.dao.NoteDAO;

import home.model.Note;

import home.model.NoteHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteDAO noteDAO;

    @Override
    @Transactional
    public List<Note> allNotes(long userId) {
        return noteDAO.allNotes(userId);
    }

    @Override
    @Transactional
    public void add(Note note, long userId) {
        noteDAO.add(note, userId);
    }

    @Override
    @Transactional
    public void delete(Note note) {
        noteDAO.delete(note);
    }

    @Override
    @Transactional
    public void edit(Note note) {
        noteDAO.edit(note);
    }

    @Override
    @Transactional
    public Note getById(int id) {
        return noteDAO.getById(id);
    }

    @Override
    @Transactional
    public List<NoteHistory> historicalNotes(int id) {
        return noteDAO.historicalNotes(id);
    }

    @Override
    @Transactional
    public boolean exportToJson(int id) {
        return noteDAO.exportToJson(id);
    }

    @Override
    @Transactional
    public boolean importFromJson(String jsonString, long userId) {
        return noteDAO.importFromJson(jsonString, userId);
    }

}
