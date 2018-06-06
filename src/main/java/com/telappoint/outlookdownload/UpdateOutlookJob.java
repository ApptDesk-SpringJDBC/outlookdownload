package com.telappoint.outlookdownload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.telappoint.outlookdownload.model.AppointmentData;
import com.telappoint.outlookdownload.model.OutLookResponse;
import com.telappoint.outlookdownload.model.OutlookSyncReq;
import com.telappoint.outlookdownload.model.ResponseModel;
import com.telappoint.outlookdownload.restclient.RestServiceClient;

/**
 * 
 * @author Balaji N
 *
 */
public class UpdateOutlookJob implements Job {
	private static Logger logger = Logger.getLogger(UpdateOutlookJob.class);
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Properties properties = getProperties("config/outlookdownload.properties");
			String url = getApptsURL(properties);
			ResponseModel responseModel = RestServiceClient.getAppts(url);
			List<AppointmentData> apptList = getApptList(responseModel);
			
			if(apptList.isEmpty()) {
				return;
			}
			
			OutlookFeedUtils outlookUtil = new OutlookFeedUtils();
			outlookUtil.saveDataToOutLook(apptList);

			String clientCode = properties.getProperty("clientcode");
			OutlookSyncReq outlookSyncReq = new OutlookSyncReq();
			outlookSyncReq.setClientCode(clientCode);
			List<AppointmentData> result = apptList.stream().filter(s -> (s.isOutlookSyncStatus())).collect(Collectors.toList());
			List<Long> confNumbers = result.stream().map(AppointmentData::getConfNumber).collect(Collectors.toList());
			outlookSyncReq.setConfNumberList(confNumbers);
			if(!confNumbers.isEmpty()) {
				logger.info("update outlook sync status to Y for the following confNumbers: "+confNumbers.toString());
				Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
				String jsonData = gson.toJson(outlookSyncReq);
				String syncUpdateURL = getUpdateSyncStatusURL(properties);
				RestServiceClient.updateOutLookSyncStatus(syncUpdateURL, jsonData);
			}
		} catch (Exception e) {
			logger.error("Exception in UpdateOutlookJob :" + e, e);
		}
	}
	
	private List<AppointmentData> getApptList(ResponseModel responseModel) {
		Gson gson = new GsonBuilder().create();
		logger.info("Appts list response json: "+responseModel.getData());
		String jsonData = gson.toJson(responseModel.getData());
		OutLookResponse response = gson.fromJson(jsonData, new TypeToken<OutLookResponse>(){}.getType());
		return response.getOutLookApptList();
	}

	private String getApptsURL(Properties properties) {
		String user = properties.getProperty("user");
		String usercode = properties.getProperty("usercode");
		String clientcode = properties.getProperty("clientcode");
		String baseURL = properties.getProperty("OUTLOOK_DOWNLOAD_RESTWS_ENDPOINT_URL");
		String url = properties.getProperty("OUTLOOK_DOWNLOAD_GET_APPTS_URI");
		
		url = url.replace("@OUTLOOK_DOWNLOAD_RESTWS_ENDPOINT_URL@", baseURL);
		url = url.replace("@CLIENTCODE@", clientcode);
		url = url.replace("@USERNAME@", user);
		url = url.replace("@PASSWORD@", usercode);
		return url;
	}
	
	private String getUpdateSyncStatusURL(Properties properties) {
		String baseURL = properties.getProperty("OUTLOOK_DOWNLOAD_RESTWS_ENDPOINT_URL");
		String url = properties.getProperty("OUTLOOK_DOWNLOAD_UPDATE_STATUS_URI");
		url = url.replace("@OUTLOOK_DOWNLOAD_RESTWS_ENDPOINT_URL@", baseURL);
		return url;
	}

	private Properties getProperties(String relativeFilePath) throws IOException {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(relativeFilePath)) {
			properties.load(inputStream);
			return properties;
		}
	}
}
