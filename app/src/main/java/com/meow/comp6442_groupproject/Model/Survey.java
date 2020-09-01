package com.meow.comp6442_groupproject.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Survey {

    private String surveyId;
    private String status;   //open or close
    private String type;     //"Public": public, "Private": private
    private String createrId;
    private String title;
    private String createdate; //"yyyy-MM-dd HH:mm"
    private String enddate;    //"yyyy-MM-dd HH:mm"
    private ArrayList<Question> questions;
    private int count;
    private HashMap<String, Boolean> userList;


    public Survey(String type, String createrId, String title, String createdate, String enddate, ArrayList<Question> questions) {
        this.status = "open";
        this.type = type;
        this.createrId = createrId;
        this.title = title;
        this.createdate = createdate;
        this.enddate = enddate;
        this.questions = new ArrayList<Question>(questions);
        this.count = 0;
        this.userList = new HashMap<String, Boolean>();
    }

    public Survey(){}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = new ArrayList<Question>(questions);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void addCount(){
        count = count + 1;
    }

    public HashMap<String, Boolean> getUserList() {
        return userList;
    }

    public void setUserList(HashMap<String, Boolean> userList) {
        this.userList = userList;
    }

    public void addUser(String uID){
        userList.put(uID,true);
    }
}
