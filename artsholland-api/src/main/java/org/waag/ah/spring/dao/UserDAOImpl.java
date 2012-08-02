package org.waag.ah.spring.dao;

import java.util.List;


import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waag.ah.model.User;
 
@Repository
public class UserDAOImpl implements UserDAO {
 
	@Autowired
  private SessionFactory sessionFactory;

  public void addUser(User user) {
      sessionFactory.getCurrentSession().save(user);
  }

  public List<User> listUser() {
      return sessionFactory.getCurrentSession().createQuery("from User")
              .list();
  }

  public void removeUser(String email) {
      User user = (User) sessionFactory.getCurrentSession().load(
              User.class, email);
      if (null != user) {
          sessionFactory.getCurrentSession().delete(user);
      }

  }
}