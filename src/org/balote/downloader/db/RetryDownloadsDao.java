package org.balote.downloader.db;

import java.util.ArrayList;

import org.balote.downloader.db.api.IRetryDownloadsDao;
import org.balote.downloader.models.DownloadDataModel;
import org.balote.downloader.models.api.IDownloadDataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class RetryDownloadsDao implements IRetryDownloadsDao {

	private static final String TAG = "RetryDownloadsDao";
	private static SQLiteDatabase db = null;
	private static RetryDownloadsDbHelper dbHelper = null;

	public RetryDownloadsDao(Context context) {
		try {
			dbHelper = RetryDownloadsDbHelper.getInstance(context);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws SQLiteException {
		db.close();
	}

	@Override
	public void open() throws SQLiteException {
		db = dbHelper.getWritableDatabase();
	}

	@Override
	public void read() throws SQLiteException {
		db = dbHelper.getReadableDatabase();
	}

	@Override
	public void clearTable() throws SQLiteException {
		db.delete(RetryDownloadsDbConstants.TABLE_NAME, "1", null);
	}

	@Override
	public ArrayList<IDownloadDataModel> getDownloadDataList()
			throws SQLiteException {

		Log.i(TAG, "getDownloadDataList()");

		ArrayList<IDownloadDataModel> list = new ArrayList<IDownloadDataModel>();

		String[] columns = new String[] {
				RetryDownloadsDbConstants.FIELD_DOWNLOAD_URL,
				RetryDownloadsDbConstants.FIELD_FILE_PATH,
				RetryDownloadsDbConstants.FIELD_CONTENT_TYPE,
				RetryDownloadsDbConstants.FIELD_IS_RESUMABLE };

		db = dbHelper.getReadableDatabase();

		Cursor c = db.query(RetryDownloadsDbConstants.TABLE_NAME, columns,
				null, null, null, null, "id ASC");

		if (c != null) {

			if (c.moveToFirst()) {

				do {

					DownloadDataModel d = new DownloadDataModel();

					d.setUrl(c.getString(c
							.getColumnIndex(RetryDownloadsDbConstants.FIELD_DOWNLOAD_URL)));
					d.setFilePath(c.getString(c
							.getColumnIndex(RetryDownloadsDbConstants.FIELD_FILE_PATH)));
					d.setContentType(c.getString(c
							.getColumnIndex(RetryDownloadsDbConstants.FIELD_CONTENT_TYPE)));

					int resumableValue = c
							.getInt(c
									.getColumnIndex(RetryDownloadsDbConstants.FIELD_IS_RESUMABLE));

					if (resumableValue == 0) {
						d.setResumable(false);
					} else {
						d.setResumable(true);
					}

					list.add(d);

				} while (c.moveToNext());
			}
		}

		c.close();

		return list;
	}

	@Override
	public void insertDownloadData(IDownloadDataModel d) throws SQLiteException {

		Log.i(TAG, "insertDownloadData()");

		ContentValues cv = new ContentValues();
		cv.put(RetryDownloadsDbConstants.FIELD_DOWNLOAD_URL,
				d.obtainDownloadUrl());
		cv.put(RetryDownloadsDbConstants.FIELD_FILE_PATH,
				d.obtainDownloadFilePath());
		cv.put(RetryDownloadsDbConstants.FIELD_CONTENT_TYPE,
				d.obtainDownloadContentType());
		cv.put(RetryDownloadsDbConstants.FIELD_IS_RESUMABLE,
				d.checkIfResumableInt());

		boolean isInsertSuccessful = db.insert(
				RetryDownloadsDbConstants.TABLE_NAME, null, cv) > 0;

		Log.d(TAG, "insertDownloadData() is insert successful: "
				+ isInsertSuccessful);
	}

	@Override
	public void insertDownloadData(String fileUrl, String filePath,
			String contentType, boolean isResumable) throws SQLiteException {

		Log.i(TAG, "insertDownloadData()");

		int isResumableInt = 0;

		if (isResumable) {
			isResumableInt = 1;
		}

		ContentValues cv = new ContentValues();
		cv.put(RetryDownloadsDbConstants.FIELD_DOWNLOAD_URL, fileUrl);
		cv.put(RetryDownloadsDbConstants.FIELD_FILE_PATH, filePath);
		cv.put(RetryDownloadsDbConstants.FIELD_CONTENT_TYPE, contentType);
		cv.put(RetryDownloadsDbConstants.FIELD_IS_RESUMABLE, isResumableInt);

		boolean isInsertSuccessful = db.insert(
				RetryDownloadsDbConstants.TABLE_NAME, null, cv) > 0;

		Log.d(TAG, "insertDownloadData() is insert successful: "
				+ isInsertSuccessful);
	}

	@Override
	public boolean deleteDownloadData(IDownloadDataModel d)
			throws SQLiteException {

		Log.w(TAG, "deleteDownloadData()");

		boolean isDeleteSuccessful = db.delete(
				RetryDownloadsDbConstants.TABLE_NAME,
				RetryDownloadsDbConstants.FIELD_DOWNLOAD_URL + "=?",
				new String[] { d.obtainDownloadUrl() }) > 0;

		Log.d(TAG, "deleteDownloadData() is delete successful: "
				+ isDeleteSuccessful);

		return isDeleteSuccessful;
	}

	@Override
	public boolean deleteDownloadData(String url) throws SQLiteException {

		Log.w(TAG, "deleteDownloadData()");

		boolean isDeleteSuccessful = db.delete(
				RetryDownloadsDbConstants.TABLE_NAME,
				RetryDownloadsDbConstants.FIELD_DOWNLOAD_URL + "=?",
				new String[] { url }) > 0;

		Log.d(TAG, "deleteDownloadData() is delete successful: "
				+ isDeleteSuccessful);

		return isDeleteSuccessful;
	}

}
