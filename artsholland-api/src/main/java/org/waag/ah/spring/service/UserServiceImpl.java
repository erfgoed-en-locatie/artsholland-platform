package org.waag.ah.spring.service;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waag.ah.model.User;
import org.waag.ah.spring.dao.UserDAO;
 
@Service
public class UserServiceImpl implements UserService {
 
    @Autowired
    private UserDAO userDAO;
     
    @Transactional
    public void addUser(User user) {
        userDAO.addUser(user);
    }
 
    @Transactional
    public List<User> listUser() {
 
        return userDAO.listUser();
    }
 
    @Transactional
    public void removeUser(String email) {
        userDAO.removeUser(email);
    }
}
