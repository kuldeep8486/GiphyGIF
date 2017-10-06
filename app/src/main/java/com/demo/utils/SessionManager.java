package com.demo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.demo.giphydemo.MainActivity;

public class SessionManager
{
	private SharedPreferences preferences;
	private Editor editor;
	private Activity activity;
	private int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "LoginPref_Giphydemo";
	private static final String IS_LOGIN = "loginStatus";

	public static final String KEY_ID = "UserId";
	public static final String KEY_SOCIAL_ID = "SocialId";
	public static final String KEY_EMAIL = "UserEmail";
	public static final String KEY_USERNAME = "UserName";
	public static final String KEY_CONTACT_NUMBER = "ContactNumber";
	public static final String KEY_IMAGE = "Image";
	public static final String KEY_GENDER = "Gender";
	public static final String KEY_BIRTHDATE = "Birthdate";

	// device info
	public static final String KEY_DEVICE_ID = "device_id";
	public static final String KEY_DEVICE_MODEL_NAME = "device_model_name";
	public static final String KEY_DEVICE_OS = "device_os";
	public static final String KEY_DEVICE_APP_VERSION = "device_app_version";
	public static final String KEY_GCM_TOKEN_ID = "GCMTokenId";

	private static final String LAST_LOGIN_TIME = "lastLoginTime";

	private static final String UNREAD_MESSAGE_COUNT = "unreadMessageCount";

	// settings
	private static final String SETTINGS_BACKGROUND_MUSIC = "isBackgroundMusicPlay";
	private static final String SETTINGS_SHOW_NOTIFICATION = "isShowNotifications";
	private static final String SETTINGS_VIBRATION = "isVibrate";
	private static final String SETTINGS_RINGTONE = "ringtone";
	private static final String SETTINGS_RINGTONE_NAME = "ringtoneName";

	private Context context;

	public SessionManager(Activity activity)
	{
		this.activity = activity;
		this.context = activity.getApplicationContext();
		preferences = activity.getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
	}

	public SessionManager(Context context)
	{
		this.context = context;
		preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
	}

	/*use details*/
	public void createLoginSession(String userid, String email, String userName, String contactNumber, String birthdate, String image, String gender)
	{
		editor = preferences.edit();

		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_ID, userid);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_USERNAME, userName);
		editor.putString(KEY_CONTACT_NUMBER, contactNumber);
		editor.putString(KEY_BIRTHDATE, birthdate);
		editor.putString(KEY_IMAGE, image);
		editor.putString(KEY_GENDER, gender);

		editor.commit();
	}

	public boolean isLoggedIn()
	{
		return preferences.getBoolean(IS_LOGIN, false);
	}

	public String getUserId()
	{
		String uid = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_ID, ""));
		return uid;
	}

	public String getEmail()
	{
		String email = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_EMAIL, ""));
		return email;
	}

	public String getUserName()
	{
		String name = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_USERNAME, ""));
		name = AppUtils.toDisplayCase(name).trim();
		return name;
	}

	public String getGender()
	{
		String name = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_GENDER, ""));
		return name;
	}

	public String getProfileImage()
	{
		String image = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_IMAGE, ""));
		return image;
	}

	public String getContactNumber()
	{
		String str = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_CONTACT_NUMBER, ""));
		return str;
	}

	public String getBirthdate()
	{
		String str = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_BIRTHDATE, ""));
		return str;
	}

	public String getUserDeviceId()
	{
		String uname = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_DEVICE_ID, ""));
		return uname;
	}

	public void setUserDeviceId(String name)
	{
		editor = preferences.edit();
		editor.putString(KEY_DEVICE_ID, name);
		editor.commit();
	}

	public String getDeviceModelName()
	{
		String uname = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_DEVICE_MODEL_NAME, ""));
		return uname;
	}

	public void setDeviceModelName(String name)
	{
		editor = preferences.edit();
		editor.putString(KEY_DEVICE_MODEL_NAME, name);
		editor.commit();
	}

	public String getDeviceOS()
	{
		String uname = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_DEVICE_OS, ""));
		return uname;
	}

	public void setDeviceOS(String name)
	{
		editor = preferences.edit();
		editor.putString(KEY_DEVICE_OS, name);
		editor.commit();
	}

	public String getDeviceAppVersion()
	{
		String uname = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_DEVICE_APP_VERSION, "1.8"));
		return uname;
	}

	public void setDeviceAppVersion(String name)
	{
		editor = preferences.edit();
		editor.putString(KEY_DEVICE_APP_VERSION, name);
		editor.commit();
	}

	public String getTokenId()
	{
		String token = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_GCM_TOKEN_ID, ""));
		return token;
	}

	public void saveTokenId(String token)
	{
		editor = preferences.edit();
		editor.putString(KEY_GCM_TOKEN_ID, token);
		editor.commit();
	}

	public void saveProfileImage(String image)
	{
		editor = preferences.edit();
		editor.putString(KEY_IMAGE, image);
		editor.commit();
	}

	public void saveEmail(String str)
	{
		editor = preferences.edit();
		editor.putString(KEY_EMAIL, str);
		editor.commit();
	}

	public void saveUserName(String str)
	{
		editor = preferences.edit();
		editor.putString(KEY_USERNAME, str);
		editor.commit();
	}

	public void saveGender(String str)
	{
		editor = preferences.edit();
		editor.putString(KEY_GENDER, str);
		editor.commit();
	}

	public void saveContactNumber(String str)
	{
		editor = preferences.edit();
		editor.putString(KEY_CONTACT_NUMBER, str);
		editor.commit();
	}

	public void saveBirthdate(String str)
	{
		editor = preferences.edit();
		editor.putString(KEY_BIRTHDATE, str);
		editor.commit();
	}

	public void saveSocialId(String str)
	{
		editor = preferences.edit();
		editor.putString(KEY_SOCIAL_ID, str);
		editor.commit();
	}

	public String getSocialId()
	{
		String str = AppUtils.getValidAPIStringResponse(preferences.getString(KEY_SOCIAL_ID, ""));
		return str;
	}

	public void saveLastLoginTime(long time)
	{
		editor = preferences.edit();
		editor.putLong(LAST_LOGIN_TIME, time);
		editor.commit();
	}

	public long getLastLoginTime()
	{
		long time = preferences.getLong(LAST_LOGIN_TIME, 0);
		return time;
	}

	public int getUnreadMessageCount()
	{
		int count = preferences.getInt(UNREAD_MESSAGE_COUNT, 0);
		return count;
	}

	public void saveUnreadMessageCount(int count)
	{
		editor = preferences.edit();
		editor.putInt(UNREAD_MESSAGE_COUNT, count);
		editor.commit();
	}

	// settings
	public boolean isShowNotifications()
	{
		boolean flag = preferences.getBoolean(SETTINGS_SHOW_NOTIFICATION, true);
		return flag;
	}

	public void saveShowNotifications(boolean flag)
	{
		editor = preferences.edit();
		editor.putBoolean(SETTINGS_SHOW_NOTIFICATION, flag);
		editor.commit();
	}

	public boolean isBackgroundMusicPlay()
	{
		boolean flag = preferences.getBoolean(SETTINGS_BACKGROUND_MUSIC, true);
		return flag;
	}

	public void saveBackgroundMusicPlay(boolean flag)
	{
		editor = preferences.edit();
		editor.putBoolean(SETTINGS_BACKGROUND_MUSIC, flag);
		editor.commit();
	}

	public boolean isVibrate()
	{
		boolean flag = preferences.getBoolean(SETTINGS_VIBRATION, true);
		return flag;
	}

	public void saveVibrate(boolean flag)
	{
		editor = preferences.edit();
		editor.putBoolean(SETTINGS_VIBRATION, flag);
		editor.commit();
	}

	public String getRingtone()
	{
		String ringtone = preferences.getString(SETTINGS_RINGTONE, "");
		return AppUtils.getValidAPIStringResponse(ringtone);
	}

	public void saveRingtone(String ringtone)
	{
		editor = preferences.edit();
		editor.putString(SETTINGS_RINGTONE, ringtone);
		editor.commit();
	}

	public String getRingtoneName()
	{
		String ringtone = preferences.getString(SETTINGS_RINGTONE_NAME, "Default");
		return AppUtils.getValidAPIStringResponse(ringtone);
	}

	public void saveRingtoneName(String ringtone)
	{
		editor = preferences.edit();
		editor.putString(SETTINGS_RINGTONE_NAME, ringtone);
		editor.commit();
	}

	public void logoutUserSession()
	{
		// Clearing all data from Shared Preferences
		try {
			editor = preferences.edit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.clear();
		editor.commit();
	}

	@SuppressLint("NewApi")
	public void logoutUser()
	{
		// Clearing all data from Shared Preferences
		try {
			editor = preferences.edit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.clear();
		editor.commit();

		try {
			activity.finishAffinity();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		try
		{
			activity.finish();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	check Internet connection
	public boolean isNetworkAvailable()
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null)
		{
			// There are no active networks.
			return false;
		}
		else
			return true;
	}
}