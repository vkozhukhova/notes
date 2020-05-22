package home.service;

import home.model.User;

public interface UserService {
    User findUserByUsername(String userName);
    User saveUser(User user);
}
