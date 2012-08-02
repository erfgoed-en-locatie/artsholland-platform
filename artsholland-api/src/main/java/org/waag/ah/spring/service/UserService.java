package org.waag.ah.spring.service;

import java.util.List;

import org.waag.ah.model.User;
 
 public interface UserService {     
    public void addUser(User user);
    public List<User> listUser();
    public void removeUser(String email);
}