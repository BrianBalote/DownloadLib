package org.balote.downloader.models;

import java.io.Serializable;

import org.balote.downloader.models.api.IDownloadDataModel;

public class DownloadDataModel implements IDownloadDataModel, Serializable {

	private static final long serialVersionUID = 2033914651716383665L;

	private String url = "";
	private String filePath = "";
	private String contentType = "";
	private boolean isResumable = true;

	public DownloadDataModel() {
	}

	public DownloadDataModel(String url, String filePath, String contentType,
			boolean isResumable) {

		this.url = url;
		this.filePath = filePath;
		this.contentType = contentType;
		this.isResumable = isResumable;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isResumable() {
		return isResumable;
	}

	public void setResumable(boolean isResumable) {
		this.isResumable = isResumable;
	}

	@Override
	public String obtainDownloadUrl() {
		return getUrl();
	}

	@Override
	public String obtainDownloadFilePath() {
		return getFilePath();
	}

	@Override
	public String obtainDownloadContentType() {
		return getContentType();
	}

	@Override
	public boolean checkIfResumable() {
		return isResumable();
	}

	@Override
	public int checkIfResumableInt() {

		if (isResumable) {
			return 1;
		}
		return 0;
	}

}
