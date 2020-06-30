package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public final class PrepareMultiThreadManager {
    String path;
    public static DiskReadThread drt;
    public static SplitMinuteThread smt;
    public static ArrayBlockingQueue<ByteBuffer> canuse = new ArrayBlockingQueue<>(16);
    public static ArrayBlockingQueue<ByteBuffer> canread = new ArrayBlockingQueue<>(16);
    public static ArrayBlockingQueue<ByteBuffer> unsolvedMinutes = new ArrayBlockingQueue<>(16);
    public static ArrayBlockingQueue<ByteBuffer> solvedMinutes = new ArrayBlockingQueue<>(16);
    public static int MAXBUFFERLEN=476824288;
    public static int DIRECT_CHUNCK_SIZE = MAXBUFFERLEN;
    public static int RAM_CHUNCK_SIZE = MAXBUFFERLEN*3;
    public static int Time_CHUNCK_SIZE = 476824288;
    public static Thread[] smbbt=new Thread[16];
    public static int THREAD_NUMBER=1;
    public static CountDownLatch endCountDown;
    PrepareMultiThreadManager(){
        endCountDown= new CountDownLatch(30);
        Thread prepareThread=new Thread(()->{





            smt=new SplitMinuteThread(RAM_CHUNCK_SIZE,Time_CHUNCK_SIZE);
            smt.LinkBlockingQueue(canuse,canread);
            smt.start();
//            System.out.println("smt启动");
//            System.out.println("第二个directbuffer加载完成");
            for(int i=0;i<THREAD_NUMBER;++i){
                smbbt[i]=new SolveMinuteByteBufferMultiThread(unsolvedMinutes,solvedMinutes);
                smbbt[i].start();
            }

            System.out.println("异步加载结束");
        });
        Thread prepareMemory=new Thread(()->{
            PrepareMultiThreadManager.solvedMinutes.add(ByteBuffer.allocate(PrepareMultiThreadManager.Time_CHUNCK_SIZE));
            canuse.add(ByteBuffer.allocateDirect(PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE));
            for(int i=0;i<THREAD_NUMBER;++i){
                solvedMinutes.add(ByteBuffer.allocate(PrepareMultiThreadManager.Time_CHUNCK_SIZE));
            }
//            canuse.add(ByteBuffer.allocateDirect(PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE));
        });
        prepareMemory.start();
        prepareThread.start();


    }
    public void setPath(String s){
        drt=new DiskReadThread(DIRECT_CHUNCK_SIZE);
        drt.LinkBlockingQueue(canuse,canread);
        path=s;
        drt.setPath(path);
    }
    public void start(){
        drt.start();

    }
    public void stop(){
        try {
            KcodeRpcMonitorImpl.ansCache=new String[1024];
            KcodeRpcMonitorImpl.checkPairCache=new ArrayList[5000];
            for(int i=0;i<1024;++i){
                KcodeRpcMonitorImpl.ansCache[i]=KcodeRpcMonitorImpl.NOANSWER;
            }
            for(int i=0;i<5000;++i){
                KcodeRpcMonitorImpl.checkPairCache[i]=KcodeRpcMonitorImpl.NOANSWERARRAY;
            }
            drt.join();
            long[] timeArray=new long[5];
            timeArray[0]=System.currentTimeMillis();
            System.out.println();
            smt.join();
            timeArray[1]=System.currentTimeMillis();
            endCountDown.await();

            while(!solvedMinutes.isEmpty()){
                solvedMinutes.poll();
            }
            timeArray[3]=System.currentTimeMillis();

            SolveRespondThread.solve();
            timeArray[4]=System.currentTimeMillis();
//            for(int a=1;a<=4;++a){
//                System.out.print(" "+(timeArray[a]-timeArray[a-1]));
//            }
//            System.out.println();
//            Analyse.findIpPair();
            System.out.println("splite耗时"+SplitMinuteThread.splitTimeUse);
            System.out.println(SplitMinuteThread.maxBisectionTimes+" "+SplitMinuteThread.maxFindTimes+" T="+PrepareMultiThreadManager.THREAD_NUMBER+" DWS="+DiskReadThread.DiskRead_waitBuffer + " SWD="+SplitMinuteThread.SplitMinute_waitBuffer + " SWM="+SplitMinuteThread.SplitMinute_waitBa);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
