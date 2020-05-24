package home.config;

import home.model.Note;
import home.model.User;
import home.service.NoteService;
import home.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.text.ParseException;
import java.util.Locale;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WebConfig extends WebMvcConfigurationSupport {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        Locale russianLocale = new Locale("ru", "RU");
        sessionLocaleResolver.setDefaultLocale(russianLocale);
        return sessionLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry ir) {
        ir.addInterceptor(localeChangeInterceptor());
    }

   /* @Autowired
    UserService userService;
    @Autowired
    NoteService noteService;

    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addFormatter(new Formatter<User>() {


            @Override
            public User parse(String s, Locale locale) throws ParseException {
                return userService.findUserByUsername(s);
            }

            @Override
            public String print(User user, Locale locale) {
                return (user != null ? user.getUsername() : "");
            }
        });

        registry.addFormatter(new Formatter<Note>() {


            @Override
            public Note parse(String s, Locale locale) throws ParseException {
                return noteService.getById(Integer.parseInt(s));
            }

            @Override
            public String print(Note note, Locale locale) {
                return (note != null ? String.valueOf(note.getId()) : "");
            }
        });
    }*/

}
