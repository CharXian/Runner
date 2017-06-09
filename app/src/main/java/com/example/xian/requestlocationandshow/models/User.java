package com.example.xian.requestlocationandshow.models;

import java.util.List;

/**
 * Created by rick-lee on 2017/6/3.
 */

public class User {

    public String uid;
    public String username;
    public List<String> groups;

    public User() {
    }

    public User(String uid, String username, List<String> groups) {
        this.uid = uid;
        this.username = username;
        this.groups = groups;
    }

    public boolean isJoinedGroup(){
        if (groups == null)
            return false;
        else
            return true;
    }

}
