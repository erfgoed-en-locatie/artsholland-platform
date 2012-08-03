package org.waag.ah.spring.service;
 
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.waag.ah.spring.model.ApiUser;
 
@Service
public class UserService {
 
	@PersistenceContext
	private EntityManager entityManager;
     
//    @Transactional
//    public void addUser(User user) {
//        userDAO.addUser(user);
//    }
 
//    @Transactional
//    public List<User> listUser() {
//        return userDAO.listUser();
//    }
 
//    @Transactional
//    public void removeUser(String email) {
//        userDAO.removeUser(email);
//    }

	public ApiUser findUserByEmail(String email) {
		Query query = entityManager.createQuery("from ApiUser a where a.email=:email").setParameter("email", email);
		try {
			ApiUser a = (ApiUser) query.getSingleResult();
			return a;
		} catch (NoResultException e) {
			return null;
		}
	}
}
