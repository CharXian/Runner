package com.example.xian.requestlocationandshow;

import com.example.xian.requestlocationandshow.data.DataManager;
import com.example.xian.requestlocationandshow.models.Group;
import com.example.xian.requestlocationandshow.models.User;

/**
 * Created by xian on 2017/6/8.
 */

public class Sample {


    {
        Group group = DataManager.getInstance().getGroupByGid("afsaff");
        User user = DataManager.getInstance().getUserByUid("dfije");
        DataManager.getInstance().isDataEnable();


    }
}
