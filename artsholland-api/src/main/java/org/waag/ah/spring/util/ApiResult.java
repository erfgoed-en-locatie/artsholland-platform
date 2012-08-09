package org.waag.ah.spring.util;

public class ApiResult {
	
	public enum ApiResultType {
    SUCCESS,
    FAILED
	}
	
	private ApiResultType result;
	
	public ApiResult(ApiResultType result) {
		setResult(result);
	}
	
	public ApiResultType getResult() {
		return result;
	}
	public void setResult(ApiResultType result) {
		this.result = result;
	}
	
}
