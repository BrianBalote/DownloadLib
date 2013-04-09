package org.balote.downloader.runnable.api;

public interface IDownloadObserver {

	public void onNotifyDownloadSuccess(String url);

	public void onNotifyDownloadFailure(String fileUrl, String filePath,
			String contentType);

	public void onNotifyFileAlreadyExist(String url);
}
