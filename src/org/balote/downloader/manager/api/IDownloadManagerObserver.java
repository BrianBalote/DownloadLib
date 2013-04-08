package org.balote.downloader.manager.api;

public interface IDownloadManagerObserver {

	public void onNotifyDownloadSuccess();
	
	public void onNotifyDownloadFailed();
	
	public void onNotifyFileAlreadyExist();
	
}
