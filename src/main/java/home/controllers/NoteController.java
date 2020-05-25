package home.controllers;

import home.model.*;
import home.service.NoteService;
import home.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class NoteController {

    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @ModelAttribute("notesList")
    public List<Note> showNotes() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            return this.noteService.allNotes(currentUser.getId());
        } else {
            return Collections.emptyList();
        }

    }

    @ModelAttribute("otherNotesMap")
    public Map<Note, Permission> showOtherNotes() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            return this.noteService.getOtherNotes(currentUser.getId());
        } else {
            return Collections.emptyMap();
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
        User currentUser = getCurrentUser();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!! START !!!!!!!!!!!");
        List<User> usersViewList = noteService.getViewUsersList(id);
        for (User u : usersViewList) {
            System.out.println(u.getUsername());
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!! FINISH !!!!!!!!!!!");
        usersViewList.add(note.getOwner());
        if (usersViewList.contains(currentUser)) {
            model.addAttribute("notesHistoryList", notesHistoryList);
            model.addAttribute("noteId", id);
            model.addAttribute("noteCreated", note.getCreationDateTime());
            return "history";
        } else {
            return "403";
        }
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable("id") int id, Model model) {
        Note note = noteService.getById(id);
        User currentUser = getCurrentUser();
        List<User> usersEditList = noteService.getEditUsersList(id);
        usersEditList.add(note.getOwner());
        if (usersEditList.contains(currentUser)) {
            model.addAttribute("note", note);
            return "editPage";
        } else {
            return "403";
        }
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
        User currentUser = getCurrentUser();
        if (currentUser.getId() == note.getOwner().getId()) {
            noteService.delete(note);
            return "redirect:/home";
        } else {
            return "403";
        }
    }

    @GetMapping("/json/{id}")
    public String exportToJson(@PathVariable("id") int id, RedirectAttributes attributes) {
        User currentUser = getCurrentUser();
        if (currentUser.getId() == noteService.getById(id).getOwner().getId()) {
            boolean exported = noteService.exportToJson(id);
            if (exported) {
                String msg = messageSource.getMessage("note.exportSuccess", null, LocaleContextHolder.getLocale());
                attributes.addFlashAttribute("export", msg + " " + System.getProperty("java.io.tmpdir") + File.separator);
            } else {
                String msg = messageSource.getMessage("note.exportFail", null, LocaleContextHolder.getLocale());
                attributes.addFlashAttribute("export", msg);
            }
            return "redirect:/home";
        } else {
            return "403";
        }
    }

    @PostMapping("/import")
    public String importFromJson(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        // check if file is empty
        if (file.isEmpty()) {
            String msg = messageSource.getMessage("note.fileImport", null, LocaleContextHolder.getLocale());
            attributes.addFlashAttribute("message", msg);
            return "redirect:/home";
        }
        try {
            String jsonString = new String(file.getBytes(), StandardCharsets.UTF_8);
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                boolean added = noteService.importFromJson(jsonString, currentUser.getId());
                if (added) {
                    String msg = messageSource.getMessage("note.importSuccess", null, LocaleContextHolder.getLocale());
                    attributes.addFlashAttribute("message", msg);
                } else {
                    String msg = messageSource.getMessage("note.importFail", null, LocaleContextHolder.getLocale());
                    attributes.addFlashAttribute("message", msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            String msg = messageSource.getMessage("note.importFail", null, LocaleContextHolder.getLocale());
            attributes.addFlashAttribute("message", msg);
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
            return "redirect:/permissions";
        }
        String msg = "";
        try {
            noteService.addPermissions(userPermissions);
            msg = messageSource.getMessage("note.addPermissionSuccess", null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            msg = messageSource.getMessage("note.addPermissionFail", null, LocaleContextHolder.getLocale());
        } finally {
            attributes.addFlashAttribute("messagePermit", msg);
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                List<User> users =  userService.getUsersExceptCurrent(currentUser);
                model.addAttribute("users", users);
            }
        }
        return "redirect:/permissions";
    }

    @GetMapping(value = "/other")
    public String otherNotes() {
        return "other";
    }

    @GetMapping(value = "/403")
    public String accessDenied() {
        return "403";
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