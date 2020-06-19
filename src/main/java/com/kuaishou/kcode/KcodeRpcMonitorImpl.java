package com.kuaishou.kcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

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
    public long fileLength = 0;
    public double prepareTime = 0;
    public double readTime = 0;
    public byte[] bytesBuffer = new byte[100 * 1024 * 1024];
    public ArrayList<MappedByteBuffer> mbArray = new ArrayList<MappedByteBuffer>();
    public File f;
    public FileChannel channel;
    public RawBufferSolve rbs = new RawBufferSolve();

    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
        prepareTimes += 1;
    }

    public void hackTime(String path, long chunck) {
        try {
            f = new File(path);
            fileLength = f.length();
            RandomAccessFile raf = new RandomAccessFile(f, "r");

            channel = raf.getChannel();
            ByteBuffer buf1 = ByteBuffer.allocateDirect((int) chunck);
            for (long i = 0; i <= fileLength; i += chunck) {
                channel.position(i);
                MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY, 0, Math.min(chunck, fileLength - i));
                mbArray.add(buff);
            }
        } catch (IOException e) {

        }

    }

    public void realPrepare(String path, long chunck, int chunckint) {

        prepareTimes++;
        try {

            File f = new File(path);
            fileLength = f.length();
            byte[] bbb = new byte[chunckint];
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            channel = raf.getChannel();
            ByteBuffer buf1 = ByteBuffer.allocateDirect((int) chunck);

            for (long i = 0; i <= fileLength; i += chunck) {
                buf1.clear();
                int readed = channel.read(buf1);
                buf1.flip();
                
//                System.out.println("limit=" + buf1.limit() + " fileLength-i=" + (fileLength - i) + "readed=" + readed);
                rbs.run(buf1, (int) chunck);
            }
            rbs.abq.add(rbs.nowTime);
            rbs.abq.add(-1);
            long startNs = nanoTime();
            rbs.solveResponder();
//            System.out.println("solveResponder 耗时(ms):" + NANOSECONDS.toMillis(nanoTime() - startNs));

            rbs.thread1.join();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("rbs.readedBytes=" + rbs.readedBytes + "readLines=" + rbs.readedLines);
    }

    //读入
    private void sleepPrepare() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final DecimalFormat DFORMAT = new DecimalFormat("#.00%");

    public void prepare(String path) {
//        P99Solve.testP99();
//        System.out.println(DFORMAT.format(0));
        int a = 1000;
        long chunck = a * 1024 * 1024;
        int chunckint = a * 1024 * 1024;
        Long startTime = System.currentTimeMillis();
        realPrepare(path, chunck, chunckint);
//            hackTime(path,chunck);
        Long endTime = System.currentTimeMillis();
        prepareTime = (endTime - startTime) * 1.0 / 1000;
//            Runtime.getRuntime().gc();
    }
    //读入


    //查询1
    public List<String> checkPair(String caller, String responder, String time) {
        int t = 0;
        try {
            t = (int) (format.parse(time).getTime() / 60000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        t-=rbs.startMinute;
        if(t>=rbs.hashM4.size() ||t<0){
            return  new ArrayList<String>();
        }
        HashMap<String, ArrayList<String>> serviceMap=rbs.hashM4.get(t);
        if (serviceMap == null||serviceMap.size()==0) {
            return new ArrayList<String>();
        }
        ArrayList<String> ans = serviceMap.get(caller + responder);
        if (ans == null) {
            return new ArrayList<String>();
        }else{
            return ans;
        }
//        if(responder.length()>0){
//            throw new ArrayIndexOutOfBoundsException("文件长度"+fileLength+"prepare时间"+prepareTime+"getTime="+readTime);
//
//        }
//        rbs.analyseHashMap();
//        checkPairTimes += 1;
//        callerSet.add(caller);
//        responderSet.add(responder);
    }

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public int getTime(String timeStr) {
        try {
            return (int) (format.parse(timeStr).getTime() / 60000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static final String NOANSWER = "-1.00%";
    private static final String ZEROANSWER = ".00%";
    //查询2
    public static CheckResponderTimePayLoad a;
    public static CheckResponderTimePayLoad b;

    public String checkResponder(String responder, String start, String end) {
//        TreeMap<Integer,CheckResponderPayLoad> tm=rbs.hashM2.get(responder);
        CheckResponderTimePayLoad[] db = rbs.hashM3.get(responder);

        if (db == null) {
            return NOANSWER;
        }
        int t1 = 0;
        int t2 = 0;
        try {
            t1 = (int) (format.parse(start).getTime() / 60000) - rbs.startMinute;
            t2 = (int) (format.parse(end).getTime() / 60000) - rbs.startMinute;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (t2 < t1) {
            return NOANSWER;
        }
        int calleeTimes = 0;

        t1 -= 1;
        if (t1 < 0) {
            t1 = 0;
        } else if (t1 > 31) {
            t1 = 31;
        }


        if (t2 < 0) {
            t2 = 0;
        } else if (t2 > 31) {
            t2 = 31;
        }
        a = db[t1];
        b = db[t2];
        calleeTimes = b.calledTimes - a.calledTimes;
        if (calleeTimes > 0) {
            return DFORMAT.format((b.rate - a.rate) / calleeTimes);
        } else {
            return NOANSWER;
        }
    }

}
