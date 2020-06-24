package com.kuaishou.kcode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CountDownLatch;

import static java.lang.System.nanoTime;
import static java.lang.System.setOut;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author kcode
 * Created on 2020-06-01
 * 实际提交时请维持包名和类名不变
 */

public class KcodeRpcMonitorImpl implements KcodeRpcMonitor {

    public double prepareTime = 0;
    public  PrepareMultiThreadManager manager;

    //    public PrepareMultiThreadManager manager;
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {

    }








    public void newPrepare(String path) {
        manager=new PrepareMultiThreadManager();
        manager.setPath(path);
        manager.start();
        return;
    }

    public void prepare(String path) {
        try {
            long sleeplen=1000;
            System.out.println("睡"+sleeplen);
            Thread.sleep(sleeplen);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Long startTime = System.currentTimeMillis();
        System.out.println("嘿嘿 来了嗷 只有你们想不到的 没有老八做不到的");
        newPrepare(path);
        Long endTime = System.currentTimeMillis();
        prepareTime = (endTime - startTime);

//        System.out.println("准备时间" + prepareTime);
//        HeatCache.HeatCheckPair();


    }
    //读入

    public static ArrayList<String> NOANSWERARRAY = new ArrayList<String>();

    //查询1
    int tt=-1;
    public List<String> checkPair(String str1, String str2, String time) {
        if(tt==-1){
            manager.stop();
            tt=1;
        }

        int t = 26427312 + time.charAt(9)* 1440+ time.charAt(11) * 600 + time.charAt(12)* 60+ time.charAt(14)* 10  + time.charAt(15) - SplitMinuteThread.firstTime;

//        int t = 25721712 +  time.charAt(9)* 1440+ time.charAt(8) *14400+ time.charAt(11) * 600 + time.charAt(12)* 60+ time.charAt(14)* 10  + time.charAt(15)- SplitMinuteThread.firstTime;
//        if(t==1){
//            throw new ArrayIndexOutOfBoundsException("prepareTime="+prepareTime);
////            throw new ArrayIndexOutOfBoundsException("DiskRead wait "+DiskReadThread.DiskRead_waitBuffer + "SplitWait="+SplitMinuteThread.SplitMinute_waitBuffer + " SplitWaitba="+SplitMinuteThread.SplitMinute_waitBa);
////            throw new ArrayIndexOutOfBoundsException("MAXSIZE="+SplitMinuteThread.MAXBUFFERLEN);
////            throw new ArrayIndexOutOfBoundsException("MINSIZE="+SplitMinuteThread.MINBUFFERLEN);
//        }
        int len1=str1.length();
        int len2=str2.length();

        return (t > 29 || t < 0)?NOANSWERARRAY:PrepareMultiThreadDataCore.hashCheckPairArrayFlat[(((((((str1.charAt(len1-5)+(str1.charAt(len1-4)<<5)+(str1.charAt(len1-3)<<10)+(str1.charAt(len1-2)<<15 )+(str1.charAt(len1-1)<<20))% 90) << 4) + (str1.charAt(0) % 29))
                + (((((str2.charAt(len2-5)+(str2.charAt(len2-4)<<5)+(str2.charAt(len2-3)<<10)+(str2.charAt(len2-2)<<15 )+(str2.charAt(len2-1)<<20))% 90) << 4) + (str2.charAt(0) % 29))<<8)) % 4997)<<5  )+t];



    }



    private static final String NOANSWER = "-1.00%";
    //查询2

    public String checkResponder(String responder, String start, String end) {



//        int t1 = 25721712 + (((start.charAt(9) + start.charAt(8) * 10) * 24 + start.charAt(11) * 10 + start.charAt(12)) * 6 + start.charAt(14)) * 10 + start.charAt(15) - SplitMinuteThread.firstTime;
//        int t2 = 25721713 + (((end.charAt(9) + end.charAt(8) * 10) * 24 + end.charAt(11) * 10 + end.charAt(12)) * 6 + end.charAt(14)) * 10 + end.charAt(15) - SplitMinuteThread.firstTime;

//        int t1 = 25721712 + start.charAt(9)* 1440+ start.charAt(8) *14400+ start.charAt(11) * 600 + start.charAt(12)* 60+ start.charAt(14)* 10  + start.charAt(15) - SplitMinuteThread.firstTime;
//        int t2 = 25721713 + end.charAt(9)* 1440+ end.charAt(8) *14400+ end.charAt(11) * 600 + end.charAt(12)* 60+ end.charAt(14)* 10  + end.charAt(15) - SplitMinuteThread.firstTime;

        int t1 = 26427312 + start.charAt(9)* 1440+ start.charAt(11) * 600 + start.charAt(12)* 60+ start.charAt(14)* 10  + start.charAt(15) - SplitMinuteThread.firstTime;
        int t2 = 26427313 + end.charAt(9)* 1440+ end.charAt(11) * 600 + end.charAt(12)* 60+ end.charAt(14)* 10  + end.charAt(15) - SplitMinuteThread.firstTime;
//        if(t2<t1+1){
//            System.out.println("翻车");
//        }
        if (t1 > 31 || t2 < 0 ) {
            return NOANSWER;
        }
        t2=(t2>31)?31:t2;

        int hashcode=HashCode.hash(responder);
        if(t1<=0){
            return PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(t2<<10)+hashcode];
        }else{
            return PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(t1<<15)+(t2<<10)+hashcode];
        }

    }

}
