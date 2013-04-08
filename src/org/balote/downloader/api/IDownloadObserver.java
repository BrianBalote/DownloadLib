package org.balote.downloader.api;

public interface IDownloadObserver {

	public void onNotifyDownloadSuccess(String url);

	public void onNotifyDownloadFailure(String fileUrl, String filePath,
			String contentType);

	public void onNotifyFileAlreadyExist();
}
