package com.demo.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.mit.mitsutils.MitsUtils;
import com.demo.giphydemo.MainActivity;
import com.demo.utils.AppUtils;
import com.demo.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class ContactsInstantSyncUtils
{
	private SessionManager sessionManager;
	private Activity activity;
	public static boolean isSaveContactInfoRunning = false;
    private ArrayList<String> monthList = new ArrayList<>();

	public ContactsInstantSyncUtils(Activity activity)
	{
		this.activity = activity;
		this.sessionManager = new SessionManager(activity);

        AppUtils.saveDeviceInfo(activity);

        monthList = new ArrayList<String>();
        monthList.add("JAN");
        monthList.add("FEB");
        monthList.add("MAR");
        monthList.add("APR");
        monthList.add("MAY");
        monthList.add("JUN");
        monthList.add("JUL");
        monthList.add("AUG");
        monthList.add("SEP");
        monthList.add("OCT");
        monthList.add("NOV");
        monthList.add("DEC");
	}

    public void saveContactInformation()
    {
        if(sessionManager.isNetworkAvailable())
        {
            new AsyncTask<Void, Void, Void>()
            {
                private boolean isToCallHandler = false;
                private ArrayList<ArrayList<ContactPojo>> listFriendsMain = new ArrayList<>();

                protected void onPreExecute()
                {
                    try
                    {
                        isSaveContactInfoRunning = true;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    super.onPreExecute();
                };

                @SuppressWarnings({ "unchecked", "rawtypes" })
                @Override
                protected Void doInBackground(Void... params)
                {
                    try
                    {
                        StringBuffer sbUpdated = new StringBuffer();
                        String sbDeleted = "";

                        ArrayList<String> listIDNumber = new ArrayList<>();
                        ArrayList<String> contactListUpdated = new ArrayList<>();
                        ArrayList<ContactLocalPojo> listContactLocal = new ArrayList<>();

                        HashMap<String, String> hashmapEmail = AppUtils.getEmailListFromDB(activity);
                        HashMap<String, String> hashmapBirthdate = AppUtils.getBirthdateListFromDB(activity);

                        if(hashmapEmail == null)
                        {
                            hashmapEmail = new HashMap<>();
                        }

                        if(hashmapBirthdate == null)
                        {
                            hashmapBirthdate = new HashMap<>();
                        }

                        Log.v("get contacts from db", "started");

                        Cursor phones = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                        while (phones.moveToNext())
                        {
                        	String contactId = AppUtils.getValidAPIStringResponse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)));
                            String timestamp = AppUtils.getValidAPIStringResponse(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
                            String name = AppUtils.getValidAPIStringResponse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

                            String phoneNumber = AppUtils.getValidPhoneNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                            if(name.length() == 0)
                            {
                            	continue;
                            }
                            
                            String idnumber = "";

                            if(phoneNumber.length() < 10)
                            {
                                continue;
                            }
                            else if (phoneNumber.length() == 10)
                            {
                                idnumber = phoneNumber;
                            }
                            else if (phoneNumber.length() > 10)
                            {
                                idnumber = phoneNumber.substring(phoneNumber.length() - 10);
                            }

                            if(listIDNumber.contains(idnumber))
                            {
                            	continue;
                            }
                            listIDNumber.add(idnumber);

                            String email = AppUtils.getValidAPIStringResponse(hashmapEmail.get(contactId));
                            String birthdate = AppUtils.getValidAPIStringResponse(hashmapBirthdate.get(contactId));

                            ContactLocalPojo contactLocalPojo = new ContactLocalPojo();
                            contactLocalPojo.setContactId(contactId);
                            contactLocalPojo.setName(name);
                            contactLocalPojo.setNumber(phoneNumber);
                            contactLocalPojo.setEmail(email);
                            contactLocalPojo.setBirthdate(birthdate);
                            contactLocalPojo.setTimestampStr(timestamp);
                            listContactLocal.add(contactLocalPojo);
                        }
                        phones.close();

                        Log.v("get contacts from db", "stopped");
                        
                        ArrayList<ContactLocalPojo> listLocalOld = getAllContactsFromDB();
                        ArrayList<ArrayList<ContactLocalPojo>> listMain = getAllUpdatedContacts(listContactLocal, listLocalOld);
                        ArrayList<ContactLocalPojo> listUpdated = new ArrayList<>();
                        ArrayList<ContactLocalPojo> listDeleted = new ArrayList<>();
                        ArrayList<ContactLocalPojo> listAdded = new ArrayList<>();

                        if(listMain.size() > 2)
                        {
                            listUpdated = listMain.get(0);
                            listDeleted = listMain.get(1);
                            listAdded = listMain.get(2);
                        }
                        else if(listMain.size() > 1)
                        {
                            listUpdated = listMain.get(0);
                            listDeleted = listMain.get(1);
                        }
                        else if(listMain.size() > 0)
                        {
                            listUpdated = listMain.get(0);
                        }

                        if(listUpdated == null)
                        {
                            listUpdated = new ArrayList<>();
                        }

                        if(listDeleted == null)
                        {
                            listDeleted = new ArrayList<>();
                        }

                        if(listAdded == null)
                        {
                            listAdded = new ArrayList<>();
                        }

                        Log.v("all list size", "updated : " + listUpdated.size() + " & deleted : " + listDeleted.size() + " & added : " + listAdded.size());

                        if(listAdded.size() > 0 || listUpdated.size() > 0)
                        {
                            for(int i=0; i<listAdded.size(); i++)
                            {
                                ContactLocalPojo contactLocalPojo = listAdded.get(i);
                                contactListUpdated.add(contactLocalPojo.getName() + "-c0r0n@t!on2016$-" + contactLocalPojo.getNumber() + "-c0r0n@t!on2016Birthdate$-" + contactLocalPojo.getBirthdate() + "-c0r0n@t!on2016Email$-" + contactLocalPojo.getEmail());
                            }

                            for(int i=0; i<listUpdated.size(); i++)
                            {
                                ContactLocalPojo contactLocalPojo = listUpdated.get(i);
                                contactListUpdated.add(contactLocalPojo.getName() + "-c0r0n@t!on2016$-" + contactLocalPojo.getNumber() + "-c0r0n@t!on2016Birthdate$-" + contactLocalPojo.getBirthdate() + "-c0r0n@t!on2016Email$-" + contactLocalPojo.getEmail());
                            }

                            HashSet hs = new HashSet();
                            hs.addAll(contactListUpdated);
                            contactListUpdated.clear();
                            contactListUpdated.addAll(hs);

                            Collections.sort(contactListUpdated);

                            for (int i = 0; i < contactListUpdated.size(); i++)
                            {
                                if(i < contactListUpdated.size()-1)
                                {
                                    if(contactListUpdated.get(i) != null
                                            && !contactListUpdated.get(i).equalsIgnoreCase("(null)")
                                            && !contactListUpdated.get(i).equalsIgnoreCase("null"))
                                    {
                                        sbUpdated.append(contactListUpdated.get(i));
                                        sbUpdated.append("-[5p!2016$-");
                                    }
                                }
                                else
                                {
                                    if(contactListUpdated.get(i) != null
                                            && !contactListUpdated.get(i).equalsIgnoreCase("(null)")
                                            && !contactListUpdated.get(i).equalsIgnoreCase("null"))
                                    {
                                        sbUpdated.append(contactListUpdated.get(i));
                                    }
                                }
                            }
                        }

                        for(int i=0; i<listDeleted.size(); i++)
                        {
                            if(sbDeleted.length() == 0)
                            {
                                sbDeleted = listDeleted.get(i).getNumber();
                            }
                            else
                            {
                                sbDeleted = sbDeleted + "," + listDeleted.get(i).getNumber();
                            }
                        }

                        if(sbUpdated.toString().length() > 0 || sbDeleted.length() > 0)
                        {
                            syncContacts(listContactLocal, sbUpdated.toString(), sbDeleted);
                        }
                        else
                        {
                            saveAllContactsToDB(listContactLocal);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }

                private void syncContacts(final ArrayList<ContactLocalPojo> listContactLocal, final String sbUpdated, final String sbDeleted)
                {
                    try
                    {
                        if(sessionManager.isNetworkAvailable())
                        {
                            HashMap<String, String> nameValuePairs = new HashMap<String, String>();

                            nameValuePairs.put("NoList", sbUpdated);
                            nameValuePairs.put("LoggedUserId", sessionManager.getUserId());
                            nameValuePairs.put("UserDeviceId", sessionManager.getUserDeviceId());
                            nameValuePairs.put("strDeleteUserContactNo", sbDeleted);

                            Log.v("instant update params", nameValuePairs.toString() + " ");

                            final String URL = "";
                            String serverResponse = AppUtils.getValidAPIStringResponse(MitsUtils.readJSONServiceUsingPOST(URL, nameValuePairs));

                            Log.v("instant update response", serverResponse + " ");

                            if(serverResponse != null)
                            {
                                JSONObject savingContactsObject = new JSONObject(serverResponse);
                                if(savingContactsObject != null)
                                {
                                    String updateContactStatus = AppUtils.getValidAPIStringResponse(savingContactsObject.getString("msg"));

                                    if(updateContactStatus.equalsIgnoreCase("Success"))
                                    {
                                        ArrayList<ContactPojo> listFriendsUpdated = parseContactArray(savingContactsObject, "UpdatedContacts");
                                        ArrayList<ContactPojo> listFriendsDeleted = parseContactArray(savingContactsObject, "DeletedContact");
                                        ArrayList<ContactPojo> listFriendsAdded = parseContactArray(savingContactsObject, "NewContacts");

                                        if(listFriendsAdded == null)
                                        {
                                            listFriendsAdded = new ArrayList<>();
                                        }
                                        if(listFriendsUpdated == null)
                                        {
                                            listFriendsUpdated = new ArrayList<>();
                                        }
                                        if(listFriendsDeleted == null)
                                        {
                                            listFriendsDeleted = new ArrayList<>();
                                        }

                                        listFriendsMain = new ArrayList<>();
                                        listFriendsMain.add(listFriendsUpdated);
                                        listFriendsMain.add(listFriendsDeleted);
                                        listFriendsMain.add(listFriendsAdded);

                                        isToCallHandler = true;

                                        saveAllContactsToDB(listContactLocal);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                private ArrayList<ContactPojo> parseContactArray(final JSONObject savingContactsObject, final String jsonArrayStr)
                {
                    ArrayList<ContactPojo> listFriends = new ArrayList<>();
                    try {
                        JSONArray contactArray = savingContactsObject.getJSONArray(jsonArrayStr);
                        for(int i=0; i<contactArray.length(); i++)
                        {
                            JSONObject jObject = contactArray.getJSONObject(i);

                            long birthdayAppId = AppUtils.getValidAPILongResponse(jObject.getString("UserId"));
                            long contactId = AppUtils.getValidAPILongResponse(jObject.getString("UserContactId"));
                            String userName = AppUtils.getValidAPIStringResponse(jObject.getString("ContactName"));
                            String contactNo = AppUtils.getValidAPIStringResponse(jObject.getString("ContactNo"));
                            String birthdate = AppUtils.getValidAPIStringResponse(jObject.getString("Birthdate"));
                            String email = AppUtils.getValidAPIStringResponse(jObject.getString("Email"));
                            String city = AppUtils.getValidAPIStringResponse(jObject.getString("City"));
                            boolean isVerified = false;
                            try {
                                isVerified = AppUtils.getValidAPIBooleanResponse(jObject.getString("IsVerified"));
                            } catch (Exception e) {
                            }

                            boolean isFromFriends = false;
                            try {
                                isFromFriends = AppUtils.getValidAPIBooleanResponse(jObject.getString("IsFromFriend"));
                            } catch (Exception e) {
                            }

                            ContactPojo contactPojo = new ContactPojo();
                            contactPojo.setVerified(isVerified);
                            contactPojo.setFromFriend(isFromFriends);
                            contactPojo.setUserId(birthdayAppId);
                            contactPojo.setContactId(contactId);
                            contactPojo.setName(userName);
                            contactPojo.setContactNumber(contactNo);
                            if(birthdate.startsWith("0"))
                            {
                                birthdate = birthdate.substring(1, birthdate.length());
                            }
                            contactPojo.setBirthdate(birthdate);
                            if(birthdate.length() > 0)
                            {
                                long millis = AppUtils.getDateAsUpcoming(birthdate);
                                contactPojo.setBirthdateMillis(millis);

                                try {
                                    String[] arr = birthdate.split("/");
                                    String dateStr = arr[0];
                                    String monthStr = arr[1];
                                    int month = Integer.parseInt(monthStr);
                                    String monthFinal = monthList.get(month-1);
                                    contactPojo.setBirthdateDateStr(dateStr);
                                    contactPojo.setBirthdateMonthStr(monthFinal);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                contactPojo.setBirthdateMillis(0);
                                contactPojo.setBirthdateDateStr("");
                                contactPojo.setBirthdateMonthStr("");
                            }
                            contactPojo.setEmail(email);
                            contactPojo.setImagepath("");
                            contactPojo.setCity(city);
                            listFriends.add(contactPojo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return listFriends;
                }

                private ArrayList<ContactLocalPojo> getAllContactsFromDB()
                {
                    ArrayList<ContactLocalPojo> listLocalOld = new ArrayList<>();
                    Log.v("get data from db", "started");
                    DatabaseHandler cdbh = new DatabaseHandler(activity);
                    SQLiteDatabase sqlDB = cdbh.getWritableDatabase();
                    try
                    {
                        Cursor userCursor = sqlDB.rawQuery("SELECT * FROM "+DatabaseHandler.USER_TABLE_LOCAL, null);
                        if (userCursor.moveToFirst())
                        {
                            do
                            {
                                String contactId = AppUtils.getValidAPIStringResponse(userCursor.getString(userCursor.getColumnIndex(DatabaseHandler.USERLOCAL_ID)));
                                String name = AppUtils.getValidAPIStringResponse(userCursor.getString(userCursor.getColumnIndex(DatabaseHandler.USERLOCAL_NAME)));
                                String number = AppUtils.getValidAPIStringResponse(userCursor.getString(userCursor.getColumnIndex(DatabaseHandler.USERLOCAL_CONTACT_NUMBER)));
                                String email = AppUtils.getValidAPIStringResponse(userCursor.getString(userCursor.getColumnIndex(DatabaseHandler.USERLOCAL_EMAIL)));
                                String birthdate = AppUtils.getValidAPIStringResponse(userCursor.getString(userCursor.getColumnIndex(DatabaseHandler.USERLOCAL_BIRTHDATE)));
                                String timestamp = AppUtils.getValidAPIStringResponse(userCursor.getString(userCursor.getColumnIndex(DatabaseHandler.USERLOCAL_TIMESTAMP)));

                                ContactLocalPojo contactLocalPojo = new ContactLocalPojo();
                                contactLocalPojo.setContactId(contactId);
                                contactLocalPojo.setName(name);
                                contactLocalPojo.setNumber(number);
                                contactLocalPojo.setEmail(email);
                                contactLocalPojo.setBirthdate(birthdate);
                                contactLocalPojo.setTimestampStr(timestamp);
                                listLocalOld.add(contactLocalPojo);
                            }while(userCursor.moveToNext());
                        }
                        userCursor.close();

                        sqlDB.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if(sqlDB != null)
                        {
                            sqlDB.close();
                        }
                    }
                    Log.v("get data from db", "stopped");
                    return listLocalOld;
                }

                private ArrayList<ArrayList<ContactLocalPojo>> getAllUpdatedContacts(final ArrayList<ContactLocalPojo> listContactLocal, final ArrayList<ContactLocalPojo> listLocalOld)
                {
                    ArrayList<ArrayList<ContactLocalPojo>> listMain = new ArrayList<>();
                    ArrayList<ContactLocalPojo> listUpdated = new ArrayList<>();
                    ArrayList<ContactLocalPojo> listDeleted = new ArrayList<>();
                    ArrayList<ContactLocalPojo> listAdded = new ArrayList<>();
                    for(int i=0; i<listContactLocal.size(); i++)
                    {
                        boolean isContactFound = false;
                        ContactLocalPojo contactLocalPojo = listContactLocal.get(i);
                        for(int j=0; j<listLocalOld.size(); j++)
                        {
                            ContactLocalPojo contactLocalOld = listLocalOld.get(j);
                            if(contactLocalPojo.getNumber().equals(contactLocalOld.getNumber()))
                            {
                                isContactFound = true;
                                if(contactLocalPojo.getTimestamp() > contactLocalOld.getTimestamp())
                                {
                                    Log.v("contact matched", contactLocalPojo.getContactId() + " : " + contactLocalPojo.getName() + " : " + contactLocalPojo.getNumber());
                                    /*if(!contactLocalPojo.getName().equals(contactLocalOld.getName()) && !contactLocalPojo.getEmail().equals(contactLocalOld.getEmail())
                                            && !contactLocalPojo.getBirthdate().equals(contactLocalOld.getBirthdate()))
                                    {
                                        continue;
                                    }
                                    else
                                    {
                                        listUpdated.add(contactLocalPojo);
                                    }*/

                                    if(!contactLocalPojo.getName().equals(contactLocalOld.getName()) || !contactLocalPojo.getEmail().equals(contactLocalOld.getEmail())
                                            || !contactLocalPojo.getBirthdate().equals(contactLocalOld.getBirthdate()))
                                    {
                                        listUpdated.add(contactLocalPojo);
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                            }
                        }

                        if(!isContactFound)
                        {
                            listAdded.add(contactLocalPojo);
                        }
                    }

                    for(int i=0; i<listLocalOld.size(); i++)
                    {
                        boolean isContactFound = false;
                        ContactLocalPojo contactLocalOld = listLocalOld.get(i);
                        for(int j=0; j<listContactLocal.size(); j++)
                        {
                            ContactLocalPojo contactLocalPojo = listContactLocal.get(j);
                            if(contactLocalOld.getNumber().equals(contactLocalPojo.getNumber()))
                            {
                                isContactFound = true;
                            }
                        }

                        if(!isContactFound)
                        {
                            listDeleted.add(contactLocalOld);
                        }
                    }

                    listMain.add(listUpdated);
                    listMain.add(listDeleted);
                    listMain.add(listAdded);

                    return listMain;
                }

                private void saveAllContactsToDB(ArrayList<ContactLocalPojo> listContactLocal)
                {
                    Log.v("insert data to db", "started");
                    DatabaseHandler cdbh = new DatabaseHandler(activity);
                    SQLiteDatabase sqlDB = cdbh.getWritableDatabase();
                    try
                    {
                        sqlDB.beginTransaction();

                        sqlDB.execSQL("DELETE from UserDataLocal");

                        for(int i=0; i<listContactLocal.size(); i++)
                        {
                            try
                            {
                                ContactLocalPojo contactLocalPojo = listContactLocal.get(i);
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseHandler.USERLOCAL_ID, contactLocalPojo.getContactId());
                                cv.put(DatabaseHandler.USERLOCAL_NAME, contactLocalPojo.getName());
                                cv.put(DatabaseHandler.USERLOCAL_CONTACT_NUMBER, contactLocalPojo.getNumber());
                                cv.put(DatabaseHandler.USERLOCAL_EMAIL, contactLocalPojo.getEmail());
                                cv.put(DatabaseHandler.USERLOCAL_BIRTHDATE, contactLocalPojo.getBirthdate());
                                cv.put(DatabaseHandler.USERLOCAL_TIMESTAMP, contactLocalPojo.getTimestampStr());

                                sqlDB.insert(DatabaseHandler.USER_TABLE_LOCAL, null, cv);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        sqlDB.setTransactionSuccessful();
                        sqlDB.endTransaction();
                        sqlDB.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if(sqlDB != null)
                        {
                            sqlDB.close();
                        }
                    }
                    Log.v("insert data to db", "stopped");
                }

                protected void onPostExecute(Void result)
                {
                    super.onPostExecute(result);

                    isSaveContactInfoRunning = false;

                    try {
                        if(isToCallHandler && MainActivity.handlerSync != null)
                        {
                            Message message = Message.obtain();
                            message.what = 200;
                            message.obj = listFriendsMain;
                            MainActivity.handlerSync.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
    }
}
