package com.meow.comp6442_groupproject.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Option implements Parcelable {

    private String optionID;
    private String content;
    private String status;    //"Y": be chose, "N": not be chose


    public Option(String optionID, String content) {
        this.optionID = optionID;
        this.content = content;
        this.status = "N";
    }

    public Option(String content) {
        this.content = content;
        this.status = "N";
    }


    public Option(){ this.status = "N";}


    protected Option(Parcel in) {
        optionID = in.readString();
        content = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(optionID);
        dest.writeString(content);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Option> CREATOR = new Creator<Option>() {
        @Override
        public Option createFromParcel(Parcel in) {
            return new Option(in);
        }

        @Override
        public Option[] newArray(int size) {
            return new Option[size];
        }
    };

    public String getOptionID() {
        return optionID;
    }

    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
