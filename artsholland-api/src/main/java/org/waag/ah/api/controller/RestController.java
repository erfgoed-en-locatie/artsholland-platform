package org.waag.ah.api.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.api.service.RestService;
import org.waag.ah.model.rdf.Event;
import org.waag.ah.model.rdf.Production;
import org.waag.ah.model.rdf.Room;
import org.waag.ah.model.rdf.Venue;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
	
	@Resource(name = "restService")
	private RestService restService;
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)	
	public @ResponseBody String getInstance(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname, @PathVariable("cidn") String cidn,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) throws IOException  {		
		return "Not yet implemented";		
	}
	
	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
	public ModelAndView getInstance(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) {	
		
		Set<?> result = restService.getList(classname, count, page);
		
		
//		ArrayList<String> kop = new ArrayList<String>();
//		ArrayList<BigDecimal> lats = new ArrayList<BigDecimal>();
//		Iterator<?> it = result.iterator();
//		while (it.hasNext()) {
//			Object n = it.next();
//			if (n instanceof Event) {
//				Event e = (Event) n;
//				Set<Production> p = e.getProductions();
//				Iterator<Production> pi = p.iterator();
//				while (pi.hasNext()) {
//					Object sks = pi.next();
//					
//					
//					//String oij = sks.getTitle();
//					//String io = sks.getCidn();
//				}
//				int s = p.size();
//			}
//			
//		
//			String visje;
//			if (n instanceof Venue) {
//				Venue e = (Venue) n;
//				lats.add(e.getLatitude());
//				Set<Room> p = e.getRooms();
//				int S = p.size();
//				if (S > 1) {
//					String ko = "dasd";
//				}
//				Iterator<Room> pi = p.iterator();
//				while (pi.hasNext()) {
//					Object sks = pi.next();
//					if (sks instanceof Room) {
//						visje  = ((Room) sks).getLabel();
//						int ds = 2;
//						org.openrdf.model.Resource r = ((Room) sks).getResource();
//						kop.add(r.toString());
//						kop.add(visje);
//					}
//					
//					//String oij = sks.getTitle();
//					//String io = sks.getCidn();
//				}
//				int s = p.size();
//			}
//			
//			
//		}
//		String json;
//		ObjectMapper mapper = new ObjectMapper();		
//		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();	    
//		try {
//			json = writer.writeValueAsString(result);
//		} catch (JsonGenerationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		
		
		//result.clear();
	  ModelAndView mav = new ModelAndView();
	  mav.setViewName("rest/json");
	  mav.addObject("result", result);
	  return mav;
	  
	}
	
	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
	public  @ResponseBody String getGeo(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {		
		return "geo";
	}
}