package com.example.xian.requestlocationandshow.models;


import java.util.List;


/**
 * Created by rick-lee on 2017/6/6.
 */

public class Group {
    public String gid;
    public String name;
    public List<String>members;

    public Group(String gid, String name, List<String> members) {
        this.gid = gid;
        this.name = name;
        this.members = members;
    }

    public Group() {}
}
