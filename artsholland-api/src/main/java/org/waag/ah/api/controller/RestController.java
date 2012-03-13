package org.waag.ah.api.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.openrdf.repository.object.RDFObject;
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
import org.waag.ah.model.rdf.ProductionImpl;
import org.waag.ah.model.rdf.Room;
import org.waag.ah.model.rdf.Venue;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
	
	@Resource(name = "restService")
	private RestService restService;
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)	
	public ModelAndView getInstance(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname, 
			@PathVariable("cidn") String cidn) throws IOException  {		
		
		Object result = restService.getInstance(classname, cidn);
	  return modelAndView(result);
		
	}
	
	
	@RequestMapping(value = MAPPING + "test", method = RequestMethod.GET)	
	public ModelAndView testDate(
			final HttpServletRequest request,	final HttpServletResponse response,			
			@RequestParam(value="before", defaultValue="0", required=false) String before,
			@RequestParam(value="after", defaultValue="0", required=false) String after) throws DatatypeConfigurationException {	
		
		
		XMLGregorianCalendar dateTimeBefore = DatatypeFactory.newInstance().newXMLGregorianCalendar(before);
		XMLGregorianCalendar dateTimeAfter = DatatypeFactory.newInstance().newXMLGregorianCalendar(after);
		
		Set<?> result = restService.getEvents(dateTimeBefore, dateTimeAfter);
		return modelAndView(result);
		
	}
	
	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
	public ModelAndView getList(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) {	
		
		Set<?> result = restService.getInstanceList(classname, count, page);
		return modelAndView(result);
		
	}
	
	@RequestMapping(value = MAPPING + "venues/{cidn}/rooms", method = RequestMethod.GET)	
	public ModelAndView getRooms(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("cidn") String cidn) {
		
		Set<?> result = restService.getRooms(cidn);
	  return modelAndView(result);	  
	  
	}
	

	private ModelAndView modelAndView(Object result) {
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
	
	
	
	@SuppressWarnings("unused")
	private void test(Set<?> result) {
		ArrayList<String> kop = new ArrayList<String>();
		ArrayList<BigDecimal> lats = new ArrayList<BigDecimal>();
		Iterator<?> it = result.iterator();
		while (it.hasNext()) {
			Object n = it.next();
			if (n instanceof Event) {
				Event e = (Event) n;
				Set<Venue> p = e.getVenues();
				Iterator<Venue> pi = p.iterator();
				while (pi.hasNext()) {
					Object sks = pi.next();
					String joi = sks.getClass().getName();
					if (sks instanceof Venue) {
						String ij = ((Venue) sks).getDescription();
						org.openrdf.model.Resource r = ((Venue) sks).getResource();
						kop.add(ij);
					}
					
					if (sks instanceof RDFObject) {
						String ko = sks.toString();
						int dsa = 12;
					}
					
					if (sks instanceof Venue) {
						String ko = sks.toString();
						int dsa = 12;
						
					}
					
				}
				int s = p.size();
			}
			
		
			String visje;
			if (n instanceof Venue) {
				Venue e = (Venue) n;
				lats.add(e.getLatitude());
				Set<Room> p = e.getRooms();
				int S = p.size();
				if (S > 1) {
					String ko = "dasd";
				}
				Iterator<Room> pi = p.iterator();
				while (pi.hasNext()) {
					Object sks = pi.next();
					if (sks instanceof Room) {
						visje  = ((Room) sks).getLabel();
						int ds = 2;
						org.openrdf.model.Resource r = ((Room) sks).getResource();
						kop.add(r.toString());
						kop.add(visje);
					}
					
					//String oij = sks.getTitle();
					//String io = sks.getCidn();
				}
				int s = p.size();
			}
			
			
		}
		String json;
		ObjectMapper mapper = new ObjectMapper();		
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();	    
		try {
			json = writer.writeValueAsString(result);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}