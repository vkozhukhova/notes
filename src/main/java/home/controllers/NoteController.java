package home.controllers;

import home.model.Note;
import home.model.NoteHistory;
import home.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/history/{id}")
    public String history(@PathVariable("id") int id, Model model) {
        List<NoteHistory> notesHistoryList = noteService.historicalNotes(id);
        model.addAttribute("notesHistoryList", notesHistoryList);
        model.addAttribute("noteId", id);
        return "history";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable("id") int id, Model model) {
        Note note = noteService.getById(id);
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
        model.addAttribute("note", note);
        return "editPage";
    }

    @PostMapping("/add")
    public String addNote(@ModelAttribute("note") Note note) {
        noteService.add(note);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable("id") int id) {
        Note note = noteService.getById(id);
        noteService.delete(note);
        return "redirect:/";
    }

    @GetMapping("/json/{id}")
    public String exportToJson(@PathVariable("id") int id) {
        noteService.exportToJson(id);
        return "redirect:/";
    }

    @PostMapping("/import")
    public String importFromJson(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/";
        }
        try {
            String jsonString = new String(file.getBytes(), StandardCharsets.UTF_8);
            noteService.importFromJson(jsonString);
            attributes.addFlashAttribute("message", "Added successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }


}