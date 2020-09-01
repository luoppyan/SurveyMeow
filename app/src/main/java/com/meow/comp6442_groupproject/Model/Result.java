package com.meow.comp6442_groupproject.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Result  {

    // Define objective
    private String surveyId;
    private String questionId;
    private String question;
    private HashMap<String, Integer> resList;

    //Set constructor
    public Result(String surveyId, String questionId, HashMap<String, Integer> resList,String question){
        this.questionId = questionId;
        this.surveyId = surveyId;
        this.resList = resList;
        this.question =question;
    }

    //Setters and getters
    public Result(){}

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getQuestionId(){return questionId;}

    public void setQuestionId(String questionId){
        this.questionId = questionId;
    }

    public HashMap<String, Integer> getResList() {
        return resList;
    }

    public void setResList(HashMap<String, Integer> resList) {
        this.resList = resList;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion(){return question;}

    public void addKeyValue(String key){
        resList.put(key, resList.get(key) + 1);
    }


}
