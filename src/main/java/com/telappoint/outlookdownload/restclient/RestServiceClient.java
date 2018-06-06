package com.telappoint.outlookdownload.restclient;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.telappoint.outlookdownload.model.ResponseModel;

/**
 * 
 * @author Balaji N
 *
 */
public class RestServiceClient {
	private static Logger logger = Logger.getLogger(RestServiceClient.class);
	
	public static ResponseModel getAppts(String url) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseJSON = restTemplate.getForEntity(url, String.class);
		Gson gson = new GsonBuilder().create();
		ResponseModel responseModel = gson.fromJson(responseJSON.getBody(), new TypeToken<ResponseModel>() {}.getType());
		return responseModel;
	}

	public static ResponseEntity<ResponseModel> updateOutLookSyncStatus(String url, String jsonPayLoadToPost) throws Exception {
		ResponseEntity<ResponseModel> responseModel = null;
		try {
			HttpHeaders requestHeaders = createHttpHeader("*/*", "UTF-8", "application/json");
			HttpEntity<String> requestEntity = new HttpEntity<String>(jsonPayLoadToPost,requestHeaders);
			RestTemplate restTemplate = new RestTemplate();
			responseModel = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ResponseModel.class);
		} catch (Exception e) {
			logger.error("UpdateOutLookSyncStatus failed." + e, e);
			throw e;
		}
		return responseModel;
	}
	
	/**
     * create HTTP headers.
     *
     * @return
     */
    private static HttpHeaders createHttpHeader(String acceptMediaType, String acceptCharset, String contentType) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Accept", acceptMediaType);
        requestHeaders.set("Accept-Charset", acceptCharset);
        requestHeaders.set("Content-Type", contentType);
        return requestHeaders;
    }
}
