package com.technophile.userformassignment.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Questions {


    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<Question> data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Question> getData() {
        return data;
    }

    public void setData(List<Question> data) {
        this.data = data;
    }

    public String serialize() {

        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public Questions create(String serializedData) {

        Gson gson = new Gson();
        return gson.fromJson(serializedData, Questions.class);
    }

}
