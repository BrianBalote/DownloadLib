package org.balote.downloader.manager.api;

public interface IDownloadManagerObserver {

	public void onNotifyDownloadSuccess(String url);
	
	public void onNotifyDownloadFailed(String url);
	
	public void onNotifyFileAlreadyFinishedDownloading(String url);
	
}
