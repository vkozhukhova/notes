package home.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import home.model.Note;
import home.model.NoteHistory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Repository
public class NoteDAOImpl implements NoteDAO {
    @PersistenceContext
    EntityManager entityManager;

   /* @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
*/
    @Override
    public List<Note> allNotes() {

        //Session session = sessionFactory.getCurrentSession();
        return entityManager.createQuery("from Note").getResultList();
    }

    @Override
    public void add(Note note) {
        //Session session = entityManager.unwrap(Session.class);
        //Session session = sessionFactory.getCurrentSession();
        note.setCreationDateTime(LocalDateTime.now());
        note.setLastEditionDateTime(LocalDateTime.now());
        entityManager.persist(note);
        //session.save(note);
    }

    @Override
    public void delete(Note note) {
        //Session session = entityManager.unwrap(Session.class);
        //Session session = sessionFactory.getCurrentSession();

        //session.delete(note);
        if (entityManager.contains(note)) {
            entityManager.remove(note);
        } else {
            Note n = entityManager.getReference(note.getClass(), note.getId());
            entityManager.remove(n);
        }
    }

    @Override
    public void edit(Note newNote) {
        Session session = entityManager.unwrap(Session.class);
        //Session session = sessionFactory.getCurrentSession();
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
        entityManager.merge(oldNote);
        //session.update(oldNote);

    }

    @Override
    public Note getById(int id) {
        //Session session = entityManager.unwrap(Session.class);
        //Session session = sessionFactory.getCurrentSession();
        return entityManager.find(Note.class, id);
        //return session.get(Note.class, id);
    }

    @Override
    public List<NoteHistory> historicalNotes(int id) {
        //Session session = entityManager.unwrap(Session.class);
        //Session session = sessionFactory.getCurrentSession();
        return entityManager.createQuery("from NoteHistory where note.id=:id").setParameter("id", id).getResultList();
    }

    @Override
    public boolean exportToJson(int id) {
        Note note = getById(id);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            String dir = System.getProperty("java.io.tmpdir") + File.separator;
            String fileName = dir +
                    String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss")))+".json";
            File jsonFile = new File(fileName);
            Files.createFile(jsonFile.toPath());
            jsonFile.setReadable(true, false);
            ow.writeValue(jsonFile, note);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean importFromJson(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Note note = mapper.readValue(jsonString, Note.class);

            //Session session = sessionFactory.getCurrentSession();
            //session.save(note);
            entityManager.persist(note);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
