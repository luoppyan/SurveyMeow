package com.meow.comp6442_groupproject.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Group {

    private String groupId;
    private String hostId;
    private String groupTitle;
    private String groupDescr;
    private HashMap<String, Boolean> members = new HashMap<>(); // The UserId of the group members
    private HashMap<String, Boolean> surveys = new HashMap<>();


    public Group(String hostId, String groupTitle, String groupDescr) {
        this.hostId = hostId;
        this.groupTitle = groupTitle;
        this.groupDescr = groupDescr;
        this.surveys = new HashMap<>();
        members = new HashMap<>();
        members.put(hostId, true);
    }

    public Group(String groupId, String hostId, String groupTitle, String groupDescr) {
        this.groupId = groupId;
        this.hostId = hostId;
        this.groupTitle = groupTitle;
        this.groupDescr = groupDescr;
    }

    public Group(String hostId){
        this.hostId = hostId;
        this.members = new HashMap<>();
        // Automatically add the host to member list
        this.members.put(hostId,true);
        this.surveys = new HashMap<>();
    }
    public Group(){}

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDescr() {
        return groupDescr;
    }

    public void setGroupDescr(String groupDescr) {
        this.groupDescr = groupDescr;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Boolean> members) {
        this.members = new HashMap<>(members);
    }

    public void addMember(String memberId) {
        members.put(memberId, true);
    }

    public HashMap<String, Boolean> getSurveys() {
        return surveys;
    }

    public void setSurveys(HashMap<String, Boolean> surveys) {
        this.surveys = surveys;
    }

    public void removeMember(User user){
        this.members.remove(user.getUserId());
    }

    /*
     add new surveyId to a Group
     */
    public void addSurvey(String surveyId) {
        if (!surveys.containsKey(surveyId)) {
            surveys.put(surveyId, true);
        }
    }
}
