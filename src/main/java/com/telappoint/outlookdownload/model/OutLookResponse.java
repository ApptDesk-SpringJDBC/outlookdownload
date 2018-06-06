package com.telappoint.outlookdownload.model;

import java.util.List;

/**
 * 
 * @author Balaji N
 *
 */
public class OutLookResponse extends BaseResponse {
	private List<AppointmentData> outLookApptList;

	public List<AppointmentData> getOutLookApptList() {
		return outLookApptList;
	}

	public void setOutLookApptList(List<AppointmentData> outLookApptList) {
		this.outLookApptList = outLookApptList;
	}
}
