package org.balote.downloader.api;

public interface IDownloadStatesDescriptor {

	public static final int DOWNLOAD_NOT_STARTED = 0;
	public static final int DOWNLOAD_IS_PAUSED = 1;
	public static final int DOWNLOAD_IN_PROGRESS = 2;
	public static final int DOWNLOAD_SUCCESS = 3;
	public static final int DOWNLOAD_FAILED = 4;
}
