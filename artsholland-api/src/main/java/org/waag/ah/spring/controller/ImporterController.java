package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.waag.ah.model.Importer;
import org.waag.ah.model.Importer.ImporterType;
import org.waag.ah.spring.model.ImporterImpl;
import org.waag.ah.spring.service.ApiUserService;
import org.waag.ah.spring.service.ImporterService;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

@Controller
public class ImporterController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(ImporterController.class);
	
	private static final String MAPPING = "/admin";

	@Autowired
	private ImporterService importerService;
		
	@RequestMapping(value=MAPPING + "/importer", method=RequestMethod.GET)
	public @ResponseBody Collection<Importer> findAll (
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		
		return importerService.getImporters();
		
		/*ImporterImpl importer = new ImporterImpl();
		importer.setName("uitbase");
		importer.setType(ImporterType.IMPORTER_TYPE_INCREMENTAL);
		
		ArrayList<ImporterImpl> list = new ArrayList<ImporterImpl>();
		list.add(importer);
		return list;*/
	}	
	
	@RequestMapping(value=MAPPING + "/importer", method=RequestMethod.POST)
	public @ResponseBody ApiResult createImporter (
			@RequestBody ImporterImpl importer,
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		// Create!
		return new ApiResult(ApiResultType.SUCCESS);
	}		
			
	@RequestMapping(value=MAPPING + "/importer/{id}", method=RequestMethod.PUT) 
	public @ResponseBody ApiResult update(
			@PathVariable long id,
			@RequestBody ImporterImpl apiUser,
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		return new ApiResult(ApiResultType.SUCCESS);
	}
	
	@RequestMapping(value=MAPPING + "/importer/{id}", method=RequestMethod.GET)
	public @ResponseBody ImporterImpl findById(
			@PathVariable long id, 
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		ImporterImpl importer = new ImporterImpl();
		importer.setName("uitbase");
		importer.setType(ImporterType.IMPORTER_TYPE_INCREMENTAL);
		return importer;
	}
	
	@RequestMapping(value=MAPPING + "/importer/{id}", method=RequestMethod.DELETE) 
	public @ResponseBody ApiResult delete(
			@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		return new ApiResult(ApiResultType.SUCCESS);
	}
	
}
