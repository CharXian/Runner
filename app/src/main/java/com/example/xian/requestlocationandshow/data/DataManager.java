package com.example.xian.requestlocationandshow.data;

import com.example.xian.requestlocationandshow.MajorMapController;
import com.example.xian.requestlocationandshow.models.Group;
import com.example.xian.requestlocationandshow.models.User;

import java.util.Map;

/**
 * Created by xian on 2017/6/8.
 */

public class DataManager {

    //use singleton-strategy
    private static DataManager instance;

    private DataManager() {}
    public static DataManager getInstance(){
        if (instance == null)
            instance = new DataManager();

        return instance;
    }


    private Map<String, User>allUsersData;
    private Map<String, Group>allGroupsData;


    public void setAllUsersData(Map<String, User> allUsersData) {
        this.allUsersData = allUsersData;
    }


    public void setAllGroupsData(Map<String, Group> allGroupsData) {
        this.allGroupsData = allGroupsData;
    }

    public boolean isDataEnable(){

        if (allUsersData != null && allGroupsData != null)
            return true;
        else
            return false;
    }

    public User getUserByUid(String uid){
        return allUsersData.get(uid);
    }

    public Group getGroupByGid(String gid){
        return allGroupsData.get(gid);
    }

    public void releaseReference(){

        instance = null;
        allGroupsData = null;
        allUsersData = null;
    }
}