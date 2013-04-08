package org.balote.downloader.db.api;

import java.util.ArrayList;

import org.balote.downloader.models.api.IDownloadDataModel;

import android.database.sqlite.SQLiteException;

public interface IRetryDownloadsDao {

	public void close() throws SQLiteException;

	public void open() throws SQLiteException;

	public void read() throws SQLiteException;

	public void clearTable() throws SQLiteException;

	public ArrayList<IDownloadDataModel> getDownloadDataList()
			throws SQLiteException;

	public void insertDownloadData(IDownloadDataModel d) throws SQLiteException;

	public void insertDownloadData(String fileUrl, String filePath,
			String contentType, boolean isResumable) throws SQLiteException;

	public boolean deleteDownloadData(IDownloadDataModel d)
			throws SQLiteException;

	public boolean deleteDownloadData(String url) throws SQLiteException;

}
