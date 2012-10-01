package org.waag.ah.tika;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.springframework.util.Assert;

public class UrlDetector implements Detector {
	private static final long serialVersionUID = -9161276980425137542L;
	private MimeTypes types = null;
	
	public UrlDetector() throws MimeTypeException, IOException {
		this.types = MimeTypesFactory.create("custom-mimetypes.xml");
	}

	@SuppressWarnings("deprecation")
	public MediaType detect(InputStream input, Metadata metadata)
			throws IOException {
		String url = metadata.get(Metadata.RESOURCE_NAME_KEY);
		Assert.notNull(url, "Resource name cannot be empty");
		return types.getMimeType(url).getType();
	}
}
