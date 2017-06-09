package com.example.xian.requestlocationandshow.firebase;


/**
 * Created by rick-lee on 2017/6/3.
 */

public interface ValueEventTrigger<E> {

    public void fetchDataFromSnapshot(E data);
    public void triggerValueEventListener();
}
