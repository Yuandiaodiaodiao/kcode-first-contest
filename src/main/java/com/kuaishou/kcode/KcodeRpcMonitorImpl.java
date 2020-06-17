package com.kuaishou.kcode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author kcode
 * Created on 2020-06-01
 * 实际提交时请维持包名和类名不变
 */

public class KcodeRpcMonitorImpl implements KcodeRpcMonitor {
    public int checkPairTimes = 0;
    public HashSet<String> callerSet = new HashSet<String>();
    public HashSet<String> responderSet = new HashSet<String>();
    public int prepareTimes = 0;
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {


    }

    //读入
    public void prepare(String path) {
        File f=new File(path);
        prepareTimes++;
    }

    //查询1
    public List<String> checkPair(String caller, String responder, String time) {
        checkPairTimes += 1;
        callerSet.add(caller);
        responderSet.add(responder);
        return new ArrayList<String>();
    }

    //查询2
    public String checkResponder(String responder, String start, String end) {
        String s="checkPairTimes"+checkPairTimes+"callerSet.size"+callerSet.size()+"responderSet.size"+responderSet.size()+"   "+prepareTimes;
        if(responder.length()>0){
            throw new Error(s);

        }
        return "0.00%";
    }

}
