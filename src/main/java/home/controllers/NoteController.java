package home.controllers;

import home.model.Note;
import home.service.NoteService;
import home.service.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class NoteController {

    @Autowired
    private NoteService noteService;

    @ModelAttribute("notesList")
    public List<Note> showNotes() {
        return this.noteService.allNotes();
    }

    @GetMapping(value = "/")
    public String main(){
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable("id") int id, Model model) {
        Note note = noteService.getById(id);
        note.setLastEditionDateTime(LocalDateTime.now());
        model.addAttribute("note", note);
        return "editPage";
    }

    @PostMapping("/edit")
    public String editNote(@ModelAttribute("note") Note note) {
        noteService.edit(note);
        return "redirect:/";
    }

    @GetMapping(value = "/add")
    public String addPage(Model model) {
        Note note = new Note();
        note.setCreationDateTime(LocalDateTime.now());
        note.setLastEditionDateTime(LocalDateTime.now());
        model.addAttribute("note", note);
        return "editPage";
    }

    @PostMapping("/add")
    public String addNote(@ModelAttribute("note") Note note) {
        noteService.add(note);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteFilm(@PathVariable("id") int id) {
        Note note = noteService.getById(id);
        noteService.delete(note);
        return "redirect:/";
    }


}