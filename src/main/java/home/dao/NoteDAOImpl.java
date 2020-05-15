package home.dao;


import home.model.Note;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class NoteDAOImpl implements NoteDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /*private static final AtomicInteger AUTO_ID = new AtomicInteger(0);
    private static Map<Integer, Note> notes = new HashMap<>();

    static {
        Note note1 = new Note();
        note1.setId(AUTO_ID.getAndIncrement());
        note1.setHeader("Header1");
        note1.setText("Text1");
        note1.setCreationDateTime(LocalDateTime.now());
        note1.setLastEditionDateTime(LocalDateTime.now());
        notes.put(note1.getId(), note1);

        Note note2 = new Note();
        note2.setId(AUTO_ID.getAndIncrement());
        note2.setHeader("Header2");
        note2.setText("Text2");
        note2.setCreationDateTime(LocalDateTime.now());
        note2.setLastEditionDateTime(LocalDateTime.now());
        notes.put(note2.getId(), note2);

        Note note3 = new Note();
        note3.setId(AUTO_ID.getAndIncrement());
        note3.setHeader("Header3");
        note3.setText("Text3");
        note3.setCreationDateTime(LocalDateTime.now());
        note3.setLastEditionDateTime(LocalDateTime.now());
        notes.put(note3.getId(), note3);

        Note note4 = new Note();
        note4.setId(AUTO_ID.getAndIncrement());
        note4.setHeader("Header4");
        note4.setText("Text4");
        note4.setCreationDateTime(LocalDateTime.now());
        note4.setLastEditionDateTime(LocalDateTime.now());
        notes.put(note4.getId(), note4);
    }*/

    @Override
    public List<Note> allNotes() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Note").list();
    }

    @Override
    public void add(Note note) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(note);
    }

    @Override
    public void delete(Note note) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(note);
    }

    @Override
    public void edit(Note note) {
        Session session = sessionFactory.getCurrentSession();
        session.update(note);
    }

    @Override
    public Note getById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Note.class, id);
    }
}
