package home.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notes")
public class Note {

    //заголовок, текст, время и дата создания, время и дата последнего редактирования.
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @Column(name = "header")
    private String header;

    @Column(name = "text")
    private String text;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "creation")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creationDateTime;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "last_edition")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastEditionDateTime;

    @JsonIgnore
    @OneToMany(mappedBy = "note", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<NoteHistory> noteHistoryList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "note", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserPermissions> userPermissions = new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getLastEditionDateTime() {
        return lastEditionDateTime;
    }

    public void setLastEditionDateTime(LocalDateTime lastEditionDateTime) {
        this.lastEditionDateTime = lastEditionDateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<NoteHistory> getNoteHistoryList() {
        return noteHistoryList;
    }

    public void setNoteHistoryList(List<NoteHistory> noteHistoryList) {
        this.noteHistoryList = noteHistoryList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", text='" + text + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", lastEditionDateTime=" + lastEditionDateTime +
                ", noteHistoryList=" + noteHistoryList +
                '}';
    }
}
