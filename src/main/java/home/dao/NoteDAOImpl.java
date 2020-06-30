package home.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import home.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Note> allNotes(long userId) {

        //Session session = sessionFactory.getCurrentSession();

        return entityManager.createQuery("from Note where owner.id=:owner_id")
                    .setParameter("owner_id", userId)
                    .getResultList();
    }

    @Override
    public void add(Note note, long userId) {
        //Session session = entityManager.unwrap(Session.class);
        //Session session = sessionFactory.getCurrentSession();
        note.setCreationDateTime(LocalDateTime.now());
        note.setLastEditionDateTime(LocalDateTime.now());
        User user = entityManager.find(User.class, userId);
        note.setOwner(user);
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
        //Session session = sessionFactory.getCurrentSession();
        Note oldNote = entityManager.find(Note.class, newNote.getId());
        //Note oldNote = session.find(Note.class, newNote.getId());
        //System.out.println("oldNote = " + oldNote);
        NoteHistory noteHistory = new NoteHistory();
        noteHistory.setNote(oldNote);
        noteHistory.setHeader(oldNote.getHeader());
        noteHistory.setText(oldNote.getText());
        noteHistory.setLastEditionDateTime(oldNote.getLastEditionDateTime());
        entityManager.persist(noteHistory);
        //session.save(noteHistory);
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
    public String exportToJson(int id) {
        Note note = getById(id);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
/*            String dir = System.getProperty("java.io.tmpdir") + File.separator;
            String fileName = dir +
                    String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss")))+".json";
            File jsonFile = new File(fileName);
            Files.createFile(jsonFile.toPath());
            jsonFile.setReadable(true, false);*/
        try {
            return ow.writeValueAsString(note);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean importFromJson(String jsonString, long userId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Note note = mapper.readValue(jsonString, Note.class);
            add(note, userId);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String exportToXml(int id) {
        Note note = getById(id);

        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(note);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean importFromXml(String xmlString, long userId) {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        Note note = null;
        try {
            note = mapper.readValue(xmlString, Note.class);
            add(note, userId);
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void addPermissions(UserPermissions userPermission) {
        entityManager.persist(userPermission);
    }

    @Override
    public Map<Note, Permission> getOtherNotes(long userId) {
        List<Object[]> resultList = entityManager.createQuery("select n.id, n.header, " +
                "n.text, n.creationDateTime, n.lastEditionDateTime, n.owner, p.permission " +
                "from Note n join UserPermissions p on n.id=p.note.id " +
                "where p.user.id=:userId").setParameter("userId", userId).getResultList();
        Map<Note, Permission> notePermissionMap = new HashMap<>();
        resultList.forEach(row -> {
            Note note = new Note();
            note.setId((Integer) row[0]);
            note.setHeader((String) row[1]);
            note.setText((String) row[2]);
            note.setCreationDateTime((LocalDateTime) row[3]);
            note.setLastEditionDateTime((LocalDateTime) row[4]);
            note.setOwner((User)row[5]);
            Permission permission = (Permission) row[6];
            notePermissionMap.put(note, permission);
        });
        return notePermissionMap;
    }

    @Override
    public List<User> getViewUsersList(int id) {
        return entityManager.createQuery("select u from User u " +
                "join UserPermissions p on u.id=p.user.id " +
                "where p.note.id=:noteId").setParameter("noteId", id).getResultList();

    }

    @Override
    public List<User> getEditUsersList(int id) {
        return entityManager.createQuery("select u from User u " +
                "join UserPermissions p on u.id=p.user.id " +
                "where p.note.id=:noteId and p.permission=:permission")
                .setParameter("noteId", id)
                .setParameter("permission", Permission.EDITION)
                .getResultList();

    }

}
