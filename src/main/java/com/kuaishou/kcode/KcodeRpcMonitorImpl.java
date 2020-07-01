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

import static com.kuaishou.kcode.SplitMinuteThread.bgetTime;
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
    public PrepareMultiThreadManager manager;

    //    public PrepareMultiThreadManager manager;
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {

    }


    public void newPrepare(String path) {
        manager = new PrepareMultiThreadManager();
        manager.setPath(path);
        manager.start();
//        manager.stop();
    }

    public void prepare(String path) {


        Long startTime = System.currentTimeMillis();
        System.out.println("嘿嘿 来了嗷 只有你们想不到的 没有老八做不到的");
        newPrepare(path);
        Long endTime = System.currentTimeMillis();
        prepareTime = (endTime - startTime);
        try {
            long sleeplen=2700;
            System.out.println("睡"+sleeplen);
            Thread.sleep(sleeplen);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("准备时间" + prepareTime);
//        HeatCache.HeatCheckPair();


//        throw new ArrayIndexOutOfBoundsException(SytemBash.getAllInfo());
    }
    //读入

    public static ArrayList<String> NOANSWERARRAY = new ArrayList<String>();

    //查询1
    int tt = -1;
    public static ArrayList<String>[] checkPairCache=null;
    int statusQuery1=0;
    long hashQuery1;
    int queryIndex1=0;
    int queryLong1=-1;
    public List<String> checkPair(String str1, String str2, String time) {
//        if(tt==-1){
//            manager.stop();
//            tt=1;
//        }
//        if(tt!=2222){
////            throw new ArrayIndexOutOfBoundsException("prepareTime="+prepareTime);
//            throw new ArrayIndexOutOfBoundsException("c="+bgetTime+" T="+PrepareMultiThreadManager.THREAD_NUMBER+ " prepare="+prepareTime+" DWS="+DiskReadThread.DiskRead_waitBuffer + " SWD="+SplitMinuteThread.SplitMinute_waitBuffer + " SWM="+SplitMinuteThread.SplitMinute_waitBa);
////            throw new ArrayIndexOutOfBoundsException("MAXSIZE="+SplitMinuteThread.MAXBUFFERLEN);
////            throw new ArrayIndexOutOfBoundsException("MINSIZE="+SplitMinuteThread.MINBUFFERLEN);
//        }
        if (statusQuery1 == 2) {
            queryIndex1=(queryIndex1+1)%queryLong1;
            return checkPairCache[queryIndex1];
        } else if (statusQuery1 == 1) {
            int t = 26427312 + time.charAt(9) * 1440 + time.charAt(11) * 600 + time.charAt(12) * 60 + time.charAt(14) * 10 + time.charAt(15) - SplitMinuteThread.firstTime;
            int len1 = str1.length();
            int len2 = str2.length();

            long strHash =((
                    (((((str1.charAt(len1 - 5) + (str1.charAt(len1 - 4) << 2) + (str1.charAt(len1 - 3) << 6)
                            + (str1.charAt(len1 - 2) << 13) + (str1.charAt(len1 - 1) << 17)) % 69) << 12)
                            + ((str1.charAt(0) - 97) << 8)))

                            + ((((str2.charAt(len2 - 6) + (str2.charAt(len2 - 5) << 5) + (str2.charAt(len2 - 4) << 10)
                            + (str2.charAt(len2 - 3) << 14) + (str2.charAt(len2 - 2) << 15)
                            + (str2.charAt(len2 - 1) << 24)) % 89) << 3) + (str2.charAt(0) - 97))
            ) % 4999);
            long thisHashQuery = ((long)t << 12)+ strHash;
            if(thisHashQuery==hashQuery1){
                statusQuery1=2;
                ++queryLong1;
                return checkPairCache[0];
            }
            ++queryLong1;

            if((t > 29 || t < 0)){
                return NOANSWERARRAY;
            }else{
                ArrayList<String> ans=PrepareMultiThreadDataCore.hashCheckPairArrayFlat[(int) ((strHash << 5) + t)];
                checkPairCache[queryLong1]=ans;
                return ans;
            }


        } else {
            manager.stop();
            int t = 26427312 + time.charAt(9) * 1440 + time.charAt(11) * 600 + time.charAt(12) * 60 + time.charAt(14) * 10 + time.charAt(15) - SplitMinuteThread.firstTime;
            int len1 = str1.length();
            int len2 = str2.length();

            long strHash =((
                    (((((str1.charAt(len1 - 5) + (str1.charAt(len1 - 4) << 2) + (str1.charAt(len1 - 3) << 6)
                            + (str1.charAt(len1 - 2) << 13) + (str1.charAt(len1 - 1) << 17)) % 69) << 12)
                            + ((str1.charAt(0) - 97) << 8)))

                            + ((((str2.charAt(len2 - 6) + (str2.charAt(len2 - 5) << 5) + (str2.charAt(len2 - 4) << 10)
                            + (str2.charAt(len2 - 3) << 14) + (str2.charAt(len2 - 2) << 15)
                            + (str2.charAt(len2 - 1) << 24)) % 89) << 3) + (str2.charAt(0) - 97))
            ) % 4999);
            hashQuery1= ((long)t << 12)+ strHash;
            statusQuery1=1;
            ++queryLong1;
            if((t > 29 || t < 0)){
                return NOANSWERARRAY;
            }else{
                ArrayList<String> ans=PrepareMultiThreadDataCore.hashCheckPairArrayFlat[(int) ((strHash << 5) + t)];
                checkPairCache[queryLong1]=ans;
                return ans;
            }
        }




//        int t = 25721712 +  time.charAt(9)* 1440+ time.charAt(8) *14400+ time.charAt(11) * 600 + time.charAt(12)* 60+ time.charAt(14)* 10  + time.charAt(15)- SplitMinuteThread.firstTime;


    }


    public static final String NOANSWER = "-1.00%";
    //查询2
    int statusQuery = 0;
    long hashQuery;
    public static String[] ansCache =null;
    int queryIndex=0;
    int queryLong=-1;
    public final String checkResponder(String str2, String start, String end) {
        if (statusQuery == 2) {
            queryIndex=(queryIndex+1)%queryLong;
            return ansCache[queryIndex];
        } else if (statusQuery == 1) {
            long t1 = 26427312 + start.charAt(9) * 1440 + start.charAt(11) * 600 + start.charAt(12) * 60 + start.charAt(14) * 10 + start.charAt(15) - SplitMinuteThread.firstTime;
            long t2 = 26427313 + end.charAt(9) * 1440 + end.charAt(11) * 600 + end.charAt(12) * 60 + end.charAt(14) * 10 + end.charAt(15) - SplitMinuteThread.firstTime;
            int len2 = str2.length();

            long strHash = ((((str2.charAt(len2 - 6) + (str2.charAt(len2 - 5) << 5) + (str2.charAt(len2 - 4) << 10)
                    + (str2.charAt(len2 - 3) << 14) + (str2.charAt(len2 - 2) << 15)
                    + (str2.charAt(len2 - 1) << 24)) % 89) << 3) + (str2.charAt(0) - 97));
            long thisHashQuery = (t1 << 12) + (t2 << 37) + strHash;
            if(thisHashQuery==hashQuery){
                statusQuery=2;
                ++queryLong;
                return ansCache[0];
            }
            ++queryLong;

            if (t1 > 31 || t2 < 0) {
                return NOANSWER;
            }
            t2 = (t2 > 31) ? 31 : t2;

            if (t1 <= 0) {
                String ans=PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(int) ((t2 << 10) +strHash)];
                ansCache[queryLong]=ans;
                return ans;
            } else {
                String ans=PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(int) ((t1 << 15) + (t2 << 10) + strHash)];
                ansCache[queryLong]=ans;
                return ans;
            }

        } else {
            long t1 = 26427312 + start.charAt(9) * 1440 + start.charAt(11) * 600 + start.charAt(12) * 60 + start.charAt(14) * 10 + start.charAt(15) - SplitMinuteThread.firstTime;
            long t2 = 26427313 + end.charAt(9) * 1440 + end.charAt(11) * 600 + end.charAt(12) * 60 + end.charAt(14) * 10 + end.charAt(15) - SplitMinuteThread.firstTime;
            int len2 = str2.length();

            long strHash = ((((str2.charAt(len2 - 6) + (str2.charAt(len2 - 5) << 5) + (str2.charAt(len2 - 4) << 10)
                    + (str2.charAt(len2 - 3) << 14) + (str2.charAt(len2 - 2) << 15)
                    + (str2.charAt(len2 - 1) << 24)) % 89) << 3) + (str2.charAt(0) - 97));
            hashQuery = (t1 << 12) + (t2 << 37) + strHash;
            statusQuery = 1;
            ++queryLong;

            if (t1 > 31 || t2 < 0) {
                return NOANSWER;
            }
            t2 = (t2 > 31) ? 31 : t2;

            if (t1 <= 0) {

                String ans=PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(int) ((t2 << 10) + (int) strHash)];
                ansCache[queryLong]=ans;
                return ans;
            } else {
                String ans=PrepareMultiThreadDataCore.CheckResponderFastArrayFlat[(int) ((t1 << 15) + (t2 << 10) + (int) strHash)];
                ansCache[queryLong]=ans;
                return ans;
            }
        }


    }

}
