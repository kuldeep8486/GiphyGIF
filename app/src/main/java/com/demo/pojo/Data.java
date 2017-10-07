package com.demo.pojo;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Data{

	@SerializedName("ChildImage")
	private String childImage;

	@SerializedName("ChildId")
	private int childId;

	@SerializedName("ChildName")
	private String childName;

	@SerializedName("CompletedDays")
	private int completedDays;

	@SerializedName("MissedDays")
	private int missedDays;

	@SerializedName("UserId")
	private int userId;

	@SerializedName("Gender")
	private String gender;

	@SerializedName("CreateDate")
	private String createDate;

	@SerializedName("Age")
	private String age;

	public void setChildImage(String childImage){
		this.childImage = childImage;
	}

	public String getChildImage(){
		return childImage;
	}

	public void setChildId(int childId){
		this.childId = childId;
	}

	public int getChildId(){
		return childId;
	}

	public void setChildName(String childName){
		this.childName = childName;
	}

	public String getChildName(){
		return childName;
	}

	public void setCompletedDays(int completedDays){
		this.completedDays = completedDays;
	}

	public int getCompletedDays(){
		return completedDays;
	}

	public void setMissedDays(int missedDays){
		this.missedDays = missedDays;
	}

	public int getMissedDays(){
		return missedDays;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setCreateDate(String createDate){
		this.createDate = createDate;
	}

	public String getCreateDate(){
		return createDate;
	}

	public void setAge(String age){
		this.age = age;
	}

	public String getAge(){
		return age;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"childImage = '" + childImage + '\'' + 
			",childId = '" + childId + '\'' + 
			",childName = '" + childName + '\'' + 
			",completedDays = '" + completedDays + '\'' + 
			",missedDays = '" + missedDays + '\'' + 
			",userId = '" + userId + '\'' + 
			",gender = '" + gender + '\'' + 
			",createDate = '" + createDate + '\'' + 
			",age = '" + age + '\'' + 
			"}";
		}
}