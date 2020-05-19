package home.dao;


import home.model.Note;
import home.model.NoteHistory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NoteDAOImpl implements NoteDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Note> allNotes() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Note").list();
    }

    @Override
    public void add(Note note) {
        Session session = sessionFactory.getCurrentSession();
        note.setCreationDateTime(LocalDateTime.now());
        note.setLastEditionDateTime(LocalDateTime.now());
        session.save(note);
    }

    @Override
    public void delete(Note note) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(note);
    }

    @Override
    public void edit(Note newNote) {
        Session session = sessionFactory.getCurrentSession();
        Note oldNote = session.find(Note.class, newNote.getId());
        System.out.println("oldNote = " + oldNote);
        NoteHistory noteHistory = new NoteHistory();
        noteHistory.setNote(oldNote);
        noteHistory.setHeader(oldNote.getHeader());
        noteHistory.setText(oldNote.getText());
        noteHistory.setLastEditionDateTime(oldNote.getLastEditionDateTime());
        session.save(noteHistory);
        oldNote.setHeader(newNote.getHeader());
        oldNote.setText(newNote.getText());
        oldNote.setLastEditionDateTime(LocalDateTime.now());
        oldNote.getNoteHistoryList().add(noteHistory);
        session.update(oldNote);

    }

    @Override
    public Note getById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Note.class, id);
    }

    @Override
    public List<NoteHistory> historicalNotes(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from NoteHistory where note.id=:id").setParameter("id", id).list();
    }

}
