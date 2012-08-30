package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

import com.petebevin.markdown.MarkdownProcessor;

@Service("documentationService")
public class DocumentationService {

	private MarkdownProcessor md;

	@PostConstruct
	public void postConstruct() {
		md = new MarkdownProcessor();
	}

	public Object render(String name) {
		try {
			Resource mdFile = new ClassPathResource("documentation/" + name + ".markdown");
			return md.markdown(streamToString(mdFile.getInputStream()));			
		} catch (IOException e) {
			return new ApiResult(ApiResultType.FAILED);
		}
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
