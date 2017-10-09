package com.demo.db;

import com.demo.utils.AppUtils;

/**
 * Created by Kuldeep Sakhiya on 08-Apr-2017.
 */

public class ContactLocalPojo
{
    private String contactId = "";
    private String name = "";
    private String number = "";
    private String email = "";
    private String birthdate = "";
    private String timestampStr = "";

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getTimestampStr() {
        return timestampStr;
    }

    public void setTimestampStr(String timestampStr) {
        this.timestampStr = timestampStr;
    }

    public long getTimestamp() {
        return AppUtils.getValidAPILongResponse(timestampStr);
    }
}
