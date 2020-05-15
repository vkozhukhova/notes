package home.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {

    //заголовок, текст, время и дата создания, время и дата последнего редактирования.
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "header")
    private String header;

    @Column(name = "text")
    private String text;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "creation")
    private LocalDateTime creationDateTime;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "last_edition")
    private LocalDateTime lastEditionDateTime;

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

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", text='" + text + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", lastEditionDateTime=" + lastEditionDateTime +
                '}';
    }
}
