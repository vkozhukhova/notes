package home.controllers;

import home.model.*;
import home.service.NoteService;
import home.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
public class NoteController {

    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;

    @ModelAttribute("notesList")
    public List<Note> showNotes() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            return this.noteService.allNotes(currentUser.getId());
        } else {
            return Collections.emptyList();
        }

    }

    @GetMapping(value = "/")
    public String main(){
        return "index";
    }


    @GetMapping(value = "/home")
    public String notes(){
        return "home";
    }


    @GetMapping("/history/{id}")
    public String history(@PathVariable("id") int id, Model model) {
        List<NoteHistory> notesHistoryList = noteService.historicalNotes(id);
        Note note = noteService.getById(id);
        model.addAttribute("notesHistoryList", notesHistoryList);
        model.addAttribute("noteId", id);
        model.addAttribute("noteCreated", note.getCreationDateTime());
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
        return "redirect:/home";
    }

    @GetMapping(value = "/add")
    public String addPage(Model model) {
        Note note = new Note();
        model.addAttribute("note", note);
        return "editPage";
    }

    @PostMapping("/add")
    public String addNote(@ModelAttribute("note") Note note) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            noteService.add(note, currentUser.getId());
        }
        return "redirect:/home";
    }

    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable("id") int id) {
        Note note = noteService.getById(id);
        noteService.delete(note);
        return "redirect:/home";
    }

    @GetMapping("/json/{id}")
    public String exportToJson(@PathVariable("id") int id, RedirectAttributes attributes) {
        boolean exported = noteService.exportToJson(id);
        if (exported) {
            attributes.addFlashAttribute("export", "Exported successfully");
        } else {
            attributes.addFlashAttribute("export", "Export error");
        }
        return "redirect:/home";
    }

    @PostMapping("/import")
    public String importFromJson(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/home";
        }
        try {
            String jsonString = new String(file.getBytes(), StandardCharsets.UTF_8);
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                boolean added = noteService.importFromJson(jsonString, currentUser.getId());
                if (added) {
                    attributes.addFlashAttribute("message", "Added successfully");
                } else {
                    attributes.addFlashAttribute("message", "Can't add note. Check file to import");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            attributes.addFlashAttribute("message", "Import error");
        }

        return "redirect:/home";
    }

    @GetMapping(value = "/permissions")
    public String addPermissions(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            UserPermissions userPermissions = new UserPermissions();
            model.addAttribute("userPermissions", userPermissions);
            List<User> users =  userService.getUsersExceptCurrent(currentUser);
            model.addAttribute("users", users);
            return "permissions";
        }
        return "redirect:/home";
    }

    @PostMapping(value = "/permissions")
    public String savePermissions(@ModelAttribute("userPermissions") UserPermissions userPermissions,
                          BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return "permissions";
        }
        try {
            noteService.addPermissions(userPermissions);
        } catch (Exception e) {
            attributes.addFlashAttribute("errormsg","Error ocurred. Maybe not unique index for User, note and permission");
        } finally {
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                List<User> users =  userService.getUsersExceptCurrent(currentUser);
                model.addAttribute("users", users);
            }
        }
        return "permissions";
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return userService.findUserByUsername(username);
        }
        return null;
    }


}