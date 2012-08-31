package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

import com.petebevin.markdown.MarkdownProcessor;

@Service("documentationService")
public class DocumentationService {

	private MarkdownProcessor md;
	private PathMatchingResourcePatternResolver rr;

	@PostConstruct
	public void postConstruct() {
		md = new MarkdownProcessor();
		rr = new PathMatchingResourcePatternResolver();
	}

	public Object render(String name) {
		try {
			
			Resource mdFile = new ClassPathResource("documentation/" + name + ".markdown");
			
			return md.markdown(streamToString(mdFile.getInputStream()));			
		} catch (IOException e) {
			return new ApiResult(ApiResultType.FAILED);
		}
	}
	
	public List<String> list() {
		List<String> docs = new ArrayList<String>();		
		try {
			Resource[] resources = rr.getResources("documentation/*.markdown");
			for (Resource resource : resources ) {
				docs.add(FilenameUtils.getBaseName(resource.getFile().toString()));
			}
		} catch (IOException e) {
			// Nothing found!
		}		

		return docs;
	}

	private static String streamToString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}


}
