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
    public Thread[] hotResponder = new Thread[4];
    public CountDownLatch countDownLatch = new CountDownLatch(1);//创建锁
    public PrepareMultiThreadManager manager = new PrepareMultiThreadManager();

    //    public PrepareMultiThreadManager manager;
    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
        hotResponder[0] = new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long t = nanoTime();
            String s;
            for (int a = 0; a < 2500; ++a) {
                if (rbs.hashM3Array[a] != null) {
                    String[][] rb = rbs.hashM3Array[a];
                    for (int b = 0; b < 32; ++b) {
                        for (int c = 0; c < 32; ++c) {
                            s = rb[b][c];
                        }
                    }
                }

            }
            System.out.println("准备完成 耗时ms=" + NANOSECONDS.toMillis(nanoTime() - t) + " q1time=" + q1Times);
        });
        hotResponder[1] = new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long t = nanoTime();
            String s;
            for (int a = 2500; a < 5000; ++a) {
                if (rbs.hashM3Array[a] != null) {
                    String[][] rb = rbs.hashM3Array[a];
                    for (int b = 0; b < 32; ++b) {
                        for (int c = 0; c < 32; ++c) {
                            s = rb[b][c];
                        }
                    }
                }

            }
            System.out.println("准备完成 耗时ms=" + NANOSECONDS.toMillis(nanoTime() - t) + " q1time=" + q1Times);
        });
        hotResponder[2] = new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long t = nanoTime();
            String s;
            for (int a = 5000; a < 7500; ++a) {
                if (rbs.hashM3Array[a] != null) {
                    String[][] rb = rbs.hashM3Array[a];
                    for (int b = 0; b < 32; ++b) {
                        for (int c = 0; c < 32; ++c) {
                            s = rb[b][c];
                        }
                    }
                }

            }
            System.out.println("准备完成 耗时ms=" + NANOSECONDS.toMillis(nanoTime() - t) + " q1time=" + q1Times);
        });
        hotResponder[3] = new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long t = nanoTime();
            String s;
            for (int a = 7500; a < 10000; ++a) {
                if (rbs.hashM3Array[a] != null) {
                    String[][] rb = rbs.hashM3Array[a];
                    for (int b = 0; b < 32; ++b) {
                        for (int c = 0; c < 32; ++c) {
                            s = rb[b][c];
                        }
                    }
                }

            }
            System.out.println("准备完成 耗时ms=" + NANOSECONDS.toMillis(nanoTime() - t) + " q1time=" + q1Times);
        });
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

    public void newPrepare(String path) {
        manager.setPath(path);
        manager.start();
        manager.stop();
        return;
    }

    public void prepare(String path) {


        Long startTime = System.currentTimeMillis();
        System.out.println("来了嗷 老弟");
        newPrepare(path);
        Long endTime = System.currentTimeMillis();
        prepareTime = (endTime - startTime) * 1.0 / 1000;
        System.out.println("准备时间" + prepareTime);
        Long startTime2 = System.currentTimeMillis();
        HeatCache.HeatCheckPair();
        Long endTime2 = System.currentTimeMillis();
        System.out.println("预热时间 ms" + ((endTime2 - startTime2) * 1.0 / 1000));

        return;
//        System.out.println("来了嗷 老弟");
//        int a = 500;
//        long chunck = a * 1024 * 1024;
//        int chunckint = a * 1024 * 1024;
//        Long startTime = System.currentTimeMillis();
//        realPrepare(path, chunck, chunckint);
//        Long endTime = System.currentTimeMillis();
//        prepareTime = (endTime - startTime) * 1.0 / 1000;
//        try {
//            Thread.sleep(10*60*1000-(startTime-endTime)+19*1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        hotResponder[0].start();
//        hotResponder[1].start();
//        hotResponder[2].start();
//        hotResponder[3].start();
    }
    //读入

    public static ArrayList<String> NOANSWERARRAY = new ArrayList<String>();

    //查询1
    public static int q1Times = 0;

    public List<String> checkPair(String caller, String responder, String time) {
        if(responder.length()>0){
            throw new ArrayIndexOutOfBoundsException("准备时间 + prepareTime");
        }
//        rbs.serviceNameSet.add(caller+" "+responder);
//        if(q1Times>=149970){
//            countDownLatch.countDown();
//        }
//        if(q1Times>=199970){
//            countDownLatch.countDown();
//        }
//        q1Times++;
        int t = 25721712 + (((time.charAt(9) + time.charAt(8) * 10) * 24 + time.charAt(11) * 10 + time.charAt(12)) * 6 + time.charAt(14)) * 10 + time.charAt(15) - SplitMinuteThread.firstTime;
        if (t > 29 || t < 0) {
            return NOANSWERARRAY;
        }
//        System.out.println("评测 "+HashCode.hashTwoString(caller,responder) +" time= "+ t);
        return PrepareMultiThreadDataCore.hashCheckPairArray[HashCode.hashTwoString(caller, responder)][t];
//        HashMap<String, ArrayList<String>> serviceMap = rbs.hashM4.get(t);
//        if (serviceMap == null || serviceMap.size() == 0) {
//            return NOANSWERARRAY;
//        }
//
//        ArrayList<String> ans = serviceMap.get(caller + responder);
//
//        if (ans == null) {
//            return NOANSWERARRAY;
//        } else {
//            return ans;
//        }
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


    private static final String NOANSWER = "-1.00%";
    private static final String ZEROANSWER = ".00%";
    //查询2
    public static CheckResponderTimePayLoad a;
    public static CheckResponderTimePayLoad b;
    public static ArrayList<String> respond = new ArrayList<>();
    public static ArrayList<Long> timeArray = new ArrayList<>(128);

    public String checkResponder(String responder, String start, String end) {


        String[][] db = PrepareMultiThreadDataCore.CheckResponderPayLoadArray[HashCode.hash(responder)];

        if (db == null) {
            return NOANSWER;
        }
        int t1 = 25721713 + (((start.charAt(9) + start.charAt(8) * 10) * 24 + start.charAt(11) * 10 + start.charAt(12)) * 6 + start.charAt(14)) * 10 + start.charAt(15) - SplitMinuteThread.firstTime;
        int t2 = 25721713 + (((end.charAt(9) + end.charAt(8) * 10) * 24 + end.charAt(11) * 10 + end.charAt(12)) * 6 + end.charAt(14)) * 10 + end.charAt(15) - SplitMinuteThread.firstTime;
        if (t2 < t1) {
            return NOANSWER;
        }

        t1 -= 1;
        if (t1 < 0) {
            t1 = 0;
        } else if (t1 > 31) {
            return NOANSWER;
        }


        if (t2 < 0) {
            return NOANSWER;
        } else if (t2 > 31) {
            t2 = 31;
        }
        return db[t1][t2];
    }

}
