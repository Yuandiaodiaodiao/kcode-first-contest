package com.kuaishou.kcode;

import java.util.HashMap;

public class QueuePayload {
    public HashMap<String, HashMap<Long, CheckPairPayLoad>>hmap;
    public int time;
    QueuePayload(){}
    QueuePayload(HashMap<String, HashMap<Long, CheckPairPayLoad>> hmapi,int timei){
        hmap=hmapi;
        time=timei;
    }
}
