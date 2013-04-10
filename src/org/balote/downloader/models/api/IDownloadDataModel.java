package org.balote.downloader.models.api;

public interface IDownloadDataModel {

	public String obtainDownloadUrl();

	public String obtainDownloadFilePath();

	public String obtainDownloadContentType();

	public boolean checkIfResumable();
	
	public int checkIfResumableInt();
}
