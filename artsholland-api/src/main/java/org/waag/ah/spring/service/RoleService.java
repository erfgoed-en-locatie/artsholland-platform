package org.waag.ah.spring.service;
 
import org.springframework.stereotype.Service;
import org.waag.ah.spring.model.RoleImpl;
 
@Service
public class RoleService extends CRUDService<RoleImpl> {

	public RoleImpl getDefaultRole() {
		return read(1);
	}
	
}
