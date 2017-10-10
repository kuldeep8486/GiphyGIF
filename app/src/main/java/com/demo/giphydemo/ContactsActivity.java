package com.demo.giphydemo;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.MyApplication;
import com.demo.classes.ConnectivityReceiver;
import com.demo.db.ContactLocalPojo;
import com.demo.db.ContactsInstantSyncUtils;
import com.demo.db.DatabaseHandler;
import com.demo.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener
{
    private Activity activity;

    private View llLoading;
    private RecyclerView rvContacts;
    private ArrayList<ContactLocalPojo> listContactLocal = new ArrayList<>();
    private ContactAdapter contactAdapter;

    private static final String TAG = "ContactsActivity";

    // instant contact sync
    public static Handler handlerSync;
    private Handler customHandlerInstantSync = new Handler();
    private MyContentObserver contentObserver = new MyContentObserver();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_main);

        setupViews();

        onclickEvents();

        loadContactsAsync();
    }

    private void setupViews()
    {
        llLoading = findViewById(R.id.llLoading);

        rvContacts = (RecyclerView) findViewById(R.id.rvCategories);
        rvContacts.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected)
    {
        try {
            if(isConnected)
            {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // instant contact sync
    private void onclickEvents()
    {
        handlerSync = new Handler(new android.os.Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                try {
                    if(msg.what == 100)
                    {
                        ContactsInstantSyncUtils contactsInstantSyncUtils = new ContactsInstantSyncUtils(activity);
                        contactsInstantSyncUtils.saveContactInformation();
                    }
                    else if(msg.what == 200)
                    {
                        Log.v(TAG, "handler call received");
                        ArrayList<ArrayList<ContactLocalPojo>> listFriendsMainUpdated = (ArrayList<ArrayList<ContactLocalPojo>>) msg.obj;
                        ArrayList<ContactLocalPojo> listUpdated = listFriendsMainUpdated.get(0);
                        ArrayList<ContactLocalPojo> listDeleted = listFriendsMainUpdated.get(1);
                        ArrayList<ContactLocalPojo> listAdded = listFriendsMainUpdated.get(2);
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

                        Log.v(TAG, "added : " + listAdded.size() + " & deleted : " + listDeleted.size() + " & updated : " + listUpdated.size());

                        contactAdapter.updateDataInstantCall(listFriendsMainUpdated);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private Runnable updateTimerThreadSync = new Runnable()
    {
        public void run()
        {
            try {
                if(ContactsInstantSyncUtils.isSaveContactInfoRunning)
                {
                    customHandlerInstantSync.postDelayed(updateTimerThreadSync, 1000);
                }
                else
                {
                    customHandlerInstantSync.removeCallbacks(updateTimerThreadSync);

                    if(handlerSync != null)
                    {
                        Message message = Message.obtain();
                        message.what = 100;
                        handlerSync.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class MyContentObserver extends ContentObserver
    {
        public MyContentObserver()
        {
            super(null);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange)
        {
            super.onChange(selfChange);
            Log.v("observer onchange", "called, selfchange = " + selfChange);
            try {
                if(ContactsInstantSyncUtils.isSaveContactInfoRunning)
                {
                    customHandlerInstantSync.postDelayed(updateTimerThreadSync, 1000);
                }
                else
                {
                    if(handlerSync != null)
                    {
                        Message message = Message.obtain();
                        message.what = 100;
                        handlerSync.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadContactsAsync()
    {
        try
        {
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected void onPreExecute()
                {
                    llLoading.setVisibility(View.VISIBLE);
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... params)
                {
                    try {
                        listContactLocal = new ArrayList<>();
                        ArrayList<String> listIDNumber = new ArrayList<>();

                        Log.v("get contacts from db", "started");

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

//                            Log.v(TAG, "contact:" + contactId + " : " + name + " : " + phoneNumber);

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
                            contactLocalPojo.setContactId(contactId + "$" + phoneNumber);
                            contactLocalPojo.setName(name);
                            contactLocalPojo.setNumber(phoneNumber);
                            contactLocalPojo.setEmail(email);
                            contactLocalPojo.setBirthdate(birthdate);
                            contactLocalPojo.setTimestampStr(timestamp);
                            listContactLocal.add(contactLocalPojo);
                        }
                        phones.close();

                        Log.v("get contacts from db", "stopped, size : " + listContactLocal.size());

                        saveAllContactsToDB(listContactLocal);

                        listContactLocal = AppUtils.sortContactList(listContactLocal);

                        Log.v("get contacts from db", "contacts sorted");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
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

                @Override
                protected void onPostExecute(Void result)
                {
                    super.onPostExecute(result);

                    try {
                        llLoading.setVisibility(View.GONE);

                        contactAdapter = new ContactAdapter(listContactLocal);
                        rvContacts.setAdapter(contactAdapter);

                        activity.getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>
    {
        private ArrayList<ContactLocalPojo> items;

        public ContactAdapter(ArrayList<ContactLocalPojo> list)
        {
            this.items = list;
        }

        public void updateDataInstantCall(final ArrayList<ArrayList<ContactLocalPojo>> listFriendsMainUpdated)
        {
            try {
                Log.v(TAG, "adapter update method called");
                ArrayList<ContactLocalPojo> listUpdated = listFriendsMainUpdated.get(0);
                ArrayList<ContactLocalPojo> listDeleted = listFriendsMainUpdated.get(1);
                ArrayList<ContactLocalPojo> listAdded = listFriendsMainUpdated.get(2);
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

                ArrayList<ContactLocalPojo> listToRemove = new ArrayList<>();
                for(int i=0; i<listDeleted.size(); i++)
                {
                    ContactLocalPojo contactPojo = listDeleted.get(i);
                    for(int j=0; j<items.size(); j++)
                    {
                        ContactLocalPojo contactPojoAdapter = items.get(j);
                        if(contactPojo.getContactId().equals(contactPojoAdapter.getContactId()))
                        {
                            listToRemove.add(contactPojoAdapter);
                        }
                    }
                }

                if(listToRemove.size() > 0)
                {
                    items.removeAll(listToRemove);
                    Log.v(TAG, "adapter : removed");
                }

                boolean isToSort = false;
                for(int i=0; i<listAdded.size(); i++)
                {
                    isToSort = true;
                    items.add(listAdded.get(i));
                    Log.v(TAG, "adapter : added");
                }

                for(int i=0; i<listUpdated.size(); i++)
                {
                    ContactLocalPojo contactPojo = listUpdated.get(i);
                    for(int j=0; j<items.size(); j++)
                    {
                        ContactLocalPojo contactPojoAdapter = items.get(j);
                        if(contactPojo.getContactId().equals(contactPojoAdapter.getContactId()))
                        {
                            Log.v(TAG, "adapter : contact id matched : " + contactPojo.getContactId());
                            contactPojoAdapter.setName(contactPojo.getName());
                            contactPojoAdapter.setNumber(contactPojo.getNumber());
                            contactPojoAdapter.setBirthdate(contactPojo.getBirthdate());
                            contactPojoAdapter.setEmail(contactPojo.getEmail());
                            items.set(j, contactPojoAdapter);
                        }
                    }
                    Log.v(TAG, "adapter : updated");
                }

                if(isToSort)
                {
                    items = AppUtils.sortContactList(items);
                    Log.v(TAG, "adapter : sorted");
                }

                notifyDataSetChanged();

                Log.v(TAG, "adapter : notified");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rowview_contact, viewGroup, false);

            return new ViewHolder(v);
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            final TextView txtName, txtDetail;

            ViewHolder(View convertView)
            {
                super(convertView);
                txtName = (TextView) convertView.findViewById(R.id.txtName);
                txtDetail = (TextView) convertView.findViewById(R.id.txtDetail);
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            try {
                final ContactLocalPojo contactLocalPojo = items.get(position);

                holder.txtName.setText(contactLocalPojo.getName());
                String detail = contactLocalPojo.getNumber();
                if(contactLocalPojo.getEmail().length() > 0)
                {
                    detail = detail + " • " + contactLocalPojo.getEmail();
                }
                holder.txtDetail.setText(detail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
