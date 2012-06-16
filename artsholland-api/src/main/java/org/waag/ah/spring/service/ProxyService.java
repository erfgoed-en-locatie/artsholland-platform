package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

@Service("proxyService")
abstract class ProxyService {
	// private Logger logger = Logger.getLogger(ProxyService.class);

	private String baseUrl;
	private HttpMethod method;

	abstract protected void beforeProxyRequest(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method);

	abstract protected void afterProxyRequest(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method);

	public void proxyQuery(HttpServletRequest request,
			HttpServletResponse response, String path) throws IOException {
		ServletOutputStream output = response.getOutputStream();

		HttpClient client = new HttpClient();

		String url = getUrl(path);

		if (request.getMethod().equals("GET")) {

			method = new GetMethod(url);

		} else if (request.getMethod().equals("POST")) {

			method = new PostMethod(url);

		} else {

			throw new NotImplementedException(
					"This proxy only supports GET and POST methods.");
		}

		beforeProxyRequest(request, response, method);

		// Execute the method
		client.executeMethod(method);

		afterProxyRequest(request, response, method);

		// response.setContentLength(getContentLength());
		stream(method.getResponseBodyAsStream(), response.getOutputStream());

		// /////////////////////////// /////////////////////////////
		// /////////////////////////// /////////////////////////////
		// InputStream input = method.getResponseBodyAsStream();
		// OutputStream output2 = response.getOutputStream();
		// //response.setContentLength(getContentLength());
		// byte[] buffer = new byte[10240];
		//
		// try {
		// for (int length = 0; (length = input.read(buffer)) > 0;) {
		// output2.write(buffer, 0, length);
		// }
		// }
		// finally {
		// try { output2.close(); } catch (IOException ignore) {}
		// try { input.close(); } catch (IOException ignore) {}
		// }
		// /////////////////////////// /////////////////////////////
		// /////////////////////////// /////////////////////////////

		// output.write(method.getResponseBody());
		// output.flush();
		// output.close();
	}

	/*
	 * TODO: move to utility class?
	 */
	public static long stream(InputStream input, OutputStream output)
			throws IOException {
		ReadableByteChannel inputChannel = null;
		WritableByteChannel outputChannel = null;

		try {
			inputChannel = Channels.newChannel(input);
			outputChannel = Channels.newChannel(output);
			ByteBuffer buffer = ByteBuffer.allocate(10240);
			long size = 0;

			while (inputChannel.read(buffer) != -1) {
				buffer.flip();
				size += outputChannel.write(buffer);
				buffer.clear();
			}

			return size;
		} finally {
			if (outputChannel != null)
				try {
					outputChannel.close();
				} catch (IOException ignore) { /**/
				}
			if (inputChannel != null)
				try {
					inputChannel.close();
				} catch (IOException ignore) { /**/
				}
		}
	}

	public void proxyQuery(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		ServletOutputStream output = response.getOutputStream();

		HttpClient client = new HttpClient();

		if (request.getMethod().equals("GET")) {

			method = new GetMethod(baseUrl);

		} else if (request.getMethod().equals("POST")) {

			method = new PostMethod(baseUrl);

		} else {

			throw new NotImplementedException(
					"This proxy only supports GET and POST methods.");
		}

		beforeProxyRequest(request, response, method);

		// Execute the method
		client.executeMethod(method);

		afterProxyRequest(request, response, method);

		output.write(method.getResponseBody());
		output.flush();
		output.close();
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String url) {
		this.baseUrl = url;
	}

	private String getUrl(String path) {
		return getBaseUrl() + path;
	}

}
