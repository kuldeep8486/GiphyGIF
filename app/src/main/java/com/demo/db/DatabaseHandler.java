package com.demo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper
{
	public String TAG = "DEMO DATABASE";
	
	private static final int DATABASE_VERSION = 1;
	
	public static final String DB_NAME = "GiphyDemo.db";
	
	public static final String USER_TABLE = "UserData";
	public static final String USER_TABLE_LOCAL = "UserDataLocal";

	public static final String USER_ID = "id";
	public static final String USER_APP_ID = "appid";
	public static final String USER_NAME = "name";
	public static final String USER_CONTACT_NUMBER = "contactnumber";
	public static final String USER_EMAIL = "email";
	public static final String USER_BIRTHDATE = "birthdate";
	
	public static final String USER_LOCAL_PATH = "localpath";
	public static final String USER_SERVER_PATH = "serverpath";
	public static final String USER_isDOWNLOADED = "isdownloaded";

	public static final String USERLOCAL_ID = "id";
	public static final String USERLOCAL_NAME = "name";
	public static final String USERLOCAL_CONTACT_NUMBER = "contactnumber";
	public static final String USERLOCAL_EMAIL = "email";
	public static final String USERLOCAL_BIRTHDATE = "birthdate";
	public static final String USERLOCAL_TIMESTAMP = "timestamp";
	
    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
	public DatabaseHandler(Context context)
	{
		super(context, DB_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase sqlDB) 
	{
		try
		{
			//default character set utf8mb4
			sqlDB.execSQL("CREATE TABLE IF NOT EXISTS "+DatabaseHandler.USER_TABLE
														+"("+DatabaseHandler.USER_ID+" TEXT PRIMARY KEY , "
															+DatabaseHandler.USER_APP_ID+" TEXT , "
															+DatabaseHandler.USER_NAME+" TEXT , "
															+DatabaseHandler.USER_EMAIL+" TEXT , "
															+DatabaseHandler.USER_BIRTHDATE+" TEXT , "
															+DatabaseHandler.USER_CONTACT_NUMBER+" TEXT , "
															+DatabaseHandler.USER_LOCAL_PATH+" TEXT , "
															+DatabaseHandler.USER_SERVER_PATH+" TEXT , "
															+DatabaseHandler.USER_isDOWNLOADED+" TEXT);");

			sqlDB.execSQL("CREATE TABLE IF NOT EXISTS UserDataLocal (id TEXT, name TEXT, contactnumber TEXT, email TEXT, birthdate TEXT, timestamp TEXT);");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.v(TAG, "Upgrading Database");
		onCreate(db);
	}
}