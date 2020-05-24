package home.service;

import home.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findUserByUsername(String userName);
    User saveUser(User user);
    List<User> getUsersExceptCurrent(User user);
}
