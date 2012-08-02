package org.waag.ah.spring.dao;

import java.util.List;

import org.waag.ah.model.User;

public interface UserDAO {
     
    public void addUser(User user);
    public List<User> listUser();
    public void removeUser(String email);
}