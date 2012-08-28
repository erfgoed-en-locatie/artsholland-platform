package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.model.Import;
import org.waag.ah.model.Importer;
import org.waag.ah.model.Importer.ImporterType;
import org.waag.ah.spring.model.AppImpl;
import org.waag.ah.spring.model.ImporterImpl;
import org.waag.ah.spring.service.ApiUserService;
import org.waag.ah.spring.service.ImportService;
import org.waag.ah.spring.service.ImporterService;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

@Controller
public class ImporterController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(ImporterController.class);
	
	private static final String MAPPING = "/admin";

	@Autowired
	private ImporterService importerService;
		
	@Autowired
	private ImportService importService;
	
	@RequestMapping(value=MAPPING + "/importer", method=RequestMethod.GET)
	public @ResponseBody Collection<Importer> findAll (
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {		
		return importerService.getImporters();
	}		
		
	@RequestMapping(value=MAPPING + "/importer/{id}", method=RequestMethod.GET)
	public @ResponseBody Importer findById(
			@PathVariable String id, 
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		return importerService.find(id);
	}
	
	@RequestMapping(value=MAPPING + "/importer/{id}/import", method=RequestMethod.GET) 
	public @ResponseBody List<Import> findAllImports(
			@PathVariable String id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		return importService.findAllByImporterId(id);				
	}	
	
}
