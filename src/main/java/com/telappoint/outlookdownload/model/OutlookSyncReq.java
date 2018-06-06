package com.telappoint.outlookdownload.model;

import java.util.List;

public class OutlookSyncReq  {
	private String clientCode;
	private List<Long> confNumberList;

	public List<Long> getConfNumberList() {
		return confNumberList;
	}

	public void setConfNumberList(List<Long> confNumberList) {
		this.confNumberList = confNumberList;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
}
