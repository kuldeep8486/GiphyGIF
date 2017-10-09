package com.demo.db;

import com.demo.utils.AppUtils;

public class ContactPojo
{
	private long userId = 0;
	private long contactId = 0;
	private String name = "";
	private String originalName = "";
	private String contactNumber = "";
	private String birthdate = "";
	private String birthdateDateStr = "";
	private String birthdateMonthStr = "";
	private String email = "";
	private String imagepath = "";
	private boolean isSelected = false;
	private boolean isMenuVisible = false;
	private long birthdateMillis = 0;
	private boolean isCelebrationDay = false;
	private String celebrationCategoryName = "";
	private long celebrationCategoryId = 0;
	private String celebrationDayImage = "";

	private boolean isExpanded = true;
	private boolean isAddressManual = false;
	private String addressline1 = "";
	private String landmark = "";
	private String zipcode = "";
	private String city = "";
	private boolean isFavorite = false;
	private boolean isVerified = false;
	private boolean isFromFriend = false;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getContactId() {
		return contactId;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public String getName() {
		return name;
	}

	public String getNameReverse()
	{
		String strReverseName = "";
		try {
			String[] strArr = name.split(" ");
			if(strArr.length > 1)
			{
				strReverseName = strArr[1] + " " + strArr[0];
			}
			else
			{
				strReverseName = strArr[0];
			}
		}
		catch (Exception e)
		{
			strReverseName = name;
			e.printStackTrace();
		}
		strReverseName = AppUtils.removeDiacriticalMarks(strReverseName.replaceAll("\\s+", ""));
		return strReverseName;
	}

	public String getNameWithoutSpace() {
		return AppUtils.removeDiacriticalMarks(name.replaceAll("\\s+", ""));
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getBirthdateDateStr() {
		return birthdateDateStr;
	}

	public void setBirthdateDateStr(String birthdateDateStr) {
		this.birthdateDateStr = birthdateDateStr;
	}

	public String getBirthdateMonthStr() {
		return birthdateMonthStr;
	}

	public void setBirthdateMonthStr(String birthdateMonthStr) {
		this.birthdateMonthStr = birthdateMonthStr;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImagepath() {
		return imagepath.replace(" ", "%20");
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isMenuVisible() {
		return isMenuVisible;
	}

	public void setMenuVisible(boolean menuVisible) {
		isMenuVisible = menuVisible;
	}

	public long getBirthdateMillis() {
		return birthdateMillis;
	}

	public void setBirthdateMillis(long birthdateMillis) {
		this.birthdateMillis = birthdateMillis;
	}

	public String getAddressline1() {
		return addressline1;
	}

	public void setAddressline1(String addressline1) {
		this.addressline1 = addressline1;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isAddressManual() {
		return isAddressManual;
	}

	public void setAddressManual(boolean isAddressManual) {
		this.isAddressManual = isAddressManual;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		isFavorite = favorite;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean verified) {
		isVerified = verified;
	}

	public boolean isFromFriend() {
		return isFromFriend;
	}

	public void setFromFriend(boolean fromFriend) {
		isFromFriend = fromFriend;
	}

	public boolean isCelebrationDay() {
		return isCelebrationDay;
	}

	public void setCelebrationDay(boolean celebrationDay) {
		isCelebrationDay = celebrationDay;
	}

	public String getCelebrationCategoryName() {
		return celebrationCategoryName;
	}

	public void setCelebrationCategoryName(String celebrationCategoryName) {
		this.celebrationCategoryName = celebrationCategoryName;
	}

	public String getCelebrationDayImage() {
		return celebrationDayImage;
	}

	public void setCelebrationDayImage(String celebrationDayImage) {
		this.celebrationDayImage = celebrationDayImage;
	}

	public long getCelebrationCategoryId() {
		return celebrationCategoryId;
	}

	public void setCelebrationCategoryId(long celebrationCategoryId) {
		this.celebrationCategoryId = celebrationCategoryId;
	}
}