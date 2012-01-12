package org.waag.ah.api.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;
import org.waag.ah.api.domain.Profile;

@Service("profileService")
public class ProfileService {
//	protected static Logger logger = Logger.getLogger("service");
	
//	@Autowired
//	EntityManagerFactory entityManagerFactory;

	public Profile get(Long id) {
		return null;
	}
	
	public Profile add(Profile profile) {		
//		EntityManager em = entityManagerFactory.createEntityManager();
		
		try {
			URL url = new URL("http://example.com/raoul");
			profile.setUrl(url);
//			em.persist(profile);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        finally {
//            if (em != null) {
//                em.close();
//            }
        }	
        
		return profile;
   }
}
