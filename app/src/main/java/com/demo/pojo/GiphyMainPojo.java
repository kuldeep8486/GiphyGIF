package com.demo.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class GiphyMainPojo {

	@SerializedName("data")
	private ArrayList<GiphyPojo> listGiphyPojo;

	public ArrayList<GiphyPojo> getListGiphyPojo() {
		return listGiphyPojo;
	}

	public void setListGiphyPojo(ArrayList<GiphyPojo> listGiphyPojo) {
		this.listGiphyPojo = listGiphyPojo;
	}
}