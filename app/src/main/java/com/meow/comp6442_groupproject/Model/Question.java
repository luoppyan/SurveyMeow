package com.meow.comp6442_groupproject.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Question implements Parcelable {

    private String questionId;
    private String type;      //"MC" or "CB": multiple choice or checkbox
    private String title;
    private String status;    //"Y": answered, "N": not answered
    private ArrayList<Option> options;
    private String resultId;


    public Question(String questionId, String type, String title, String status, ArrayList<Option> options) {
        this.questionId = questionId;
        this.type = type;
        this.title = title;
        this.status = status;
        this.options = new ArrayList<Option>(options);
    }

    public Question(String title, String type, ArrayList<Option> options){
        this.type = type;
        this.title = title;
        this.status = "N";
        this.options = new ArrayList<Option>(options);

    }

    public Question(){}

    protected Question(Parcel in) {
        questionId = in.readString();
        type = in.readString();
        title = in.readString();
        status = in.readString();
        options = new ArrayList<Option>();
        in.readList(options, Option.class.getClassLoader());
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(questionId);
        parcel.writeString(type);
        parcel.writeString(title);
        parcel.writeString(status);
        parcel.writeList(options);
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = new ArrayList<Option>(options);
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public void addOption(Option option) {
        options.add(option);
    }
}
