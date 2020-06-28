package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class PrepareMultiThreadManager {
    String path;
    public static DiskReadThread drt;
    public static SplitMinuteThread smt;
    public static BufferCopyThread bct;
    public static ArrayBlockingQueue<ByteBuffer> canuse = new ArrayBlockingQueue<>(16);
    public static ArrayBlockingQueue<ByteBuffer> canread = new ArrayBlockingQueue<>(16);

    public static ArrayBlockingQueue<BufferPayload> remaining =new ArrayBlockingQueue<>(64);
    public static ArrayBlockingQueue<ArrayList<ByteBuffer>> unsolvedMinutes = new ArrayBlockingQueue<>(64);
    public static ArrayBlockingQueue<ByteBuffer> solvedMinutes = new ArrayBlockingQueue<>(64);
    public static int MAXBUFFERLEN=476824288;
    public static int DIRECT_CHUNCK_SIZE = MAXBUFFERLEN;
    public static int RAM_CHUNCK_SIZE = MAXBUFFERLEN*3;
    public static int Time_CHUNCK_SIZE = MAXBUFFERLEN;
    public static Thread[] smbbt=new Thread[16];
    public static int THREAD_NUMBER=7;
    public static  MultiByteBuffer buffer = new MultiByteBuffer();
    PrepareMultiThreadManager(){
//        buffer.prepareMemory();
//        System.out.println("准备完成");
        Thread prepareThread=new Thread(()->{




            bct=new BufferCopyThread();
            bct.LinkBlockingQueue(canuse,canread,remaining);
            bct.buffer=buffer;
            bct.start();
            smt=new SplitMinuteThread(RAM_CHUNCK_SIZE,Time_CHUNCK_SIZE);
            smt.buffer=buffer;
            smt.LinkBlockingQueue(remaining);
            smt.start();
//            System.out.println("smt启动");
//            System.out.println("第二个directbuffer加载完成");
            for(int i=0;i<THREAD_NUMBER;++i){
                smbbt[i]=new SolveMinuteByteBufferThread(unsolvedMinutes,solvedMinutes);
                smbbt[i].start();
            }

            System.out.println("异步加载结束");
        });
        Thread prepareMemory=new Thread(()->{
            canuse.add(ByteBuffer.allocateDirect(PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE));
            buffer.allocate();
            PrepareMultiThreadManager.solvedMinutes.add(ByteBuffer.allocate(PrepareMultiThreadManager.Time_CHUNCK_SIZE));
            canuse.add(ByteBuffer.allocateDirect(PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE));
//            buffer.prepareMemory();

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
            drt.join();
            System.out.println("读完");
            long[] timeArray=new long[5];
            timeArray[0]=System.currentTimeMillis();
            System.out.println();
            smt.join();
            smt.freeMemory();
            timeArray[1]=System.currentTimeMillis();



            for(int i=0;i<THREAD_NUMBER;++i){
                smbbt[i].join();
            }
            timeArray[2]=System.currentTimeMillis();

            while(!solvedMinutes.isEmpty()){
                solvedMinutes.poll();
            }
            timeArray[3]=System.currentTimeMillis();

            SolveRespondThread.solve();
            timeArray[4]=System.currentTimeMillis();
            for(int a=1;a<=4;++a){
                System.out.print(" "+(timeArray[a]-timeArray[a-1]));
            }
            System.out.println();
//            Analyse.findIpPair();
            System.out.println("splite耗时"+SplitMinuteThread.splitTimeUse);
            System.out.println(SplitMinuteThread.maxBisectionTimes+" "+SplitMinuteThread.maxFindTimes+" T="+PrepareMultiThreadManager.THREAD_NUMBER+" DWS="+DiskReadThread.DiskRead_waitBuffer + " SWD="+SplitMinuteThread.SplitMinute_waitBuffer + " SWM="+SplitMinuteThread.SplitMinute_waitBa);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
