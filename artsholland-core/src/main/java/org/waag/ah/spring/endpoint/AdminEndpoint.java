package org.waag.ah.spring.endpoint;
//package org.waag.ah.spring.endpoint;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ws.server.endpoint.annotation.Endpoint;
//import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
//import org.springframework.ws.server.endpoint.annotation.RequestPayload;
//import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
//import org.w3c.dom.Element;
//import org.waag.ah.ApiService;
//import org.waag.ah.api.ApplicationListResponse;
//import org.waag.ah.api.ObjectFactory;
//import org.waag.ah.model.Application;
//import org.waag.ah.spring.service.AdminService;
//
//@Endpoint
//public class AdminEndpoint implements ApiService {
////	final static Logger logger = LoggerFactory.getLogger(AdminEndpoint.class);
//	private static final String NAMESPACE_URI = "http://artsholland.com/schema";
//	private final AdminService adminService;
//	private ObjectFactory objectFactory;
//	
//	@Autowired
//	public AdminEndpoint(AdminService adminService) {
//		this.adminService = adminService;
//		this.objectFactory = new ObjectFactory();
//	}
//	
//	@SuppressWarnings("unchecked")
//	@PayloadRoot(namespace=NAMESPACE_URI, localPart="ApplicationListRequest")                        
//	public @ResponsePayload ApplicationListResponse getApplicationList(
//			@RequestPayload Element appElement) {                              
//		List<Application> result = adminService.getApplicationList();
//		ApplicationListResponse response = 
//				objectFactory.createApplicationListResponse();
////		response.getApplication().addAll(result);
////				(Collection<? extends ApplicationType>) result);
//		return response;
//	}
//}
