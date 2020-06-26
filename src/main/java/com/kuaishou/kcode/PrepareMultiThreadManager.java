package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class PrepareMultiThreadManager {
    String path;
    public static DiskReadThread drt;
    public static SplitMinuteThread smt;
    public static ArrayBlockingQueue<ByteBuffer> canuse = new ArrayBlockingQueue<>(16);
    public static ArrayBlockingQueue<ByteBuffer> canread = new ArrayBlockingQueue<>(16);
    public static ArrayBlockingQueue<ByteBuffer> unsolvedMinutes = new ArrayBlockingQueue<>(64);
    public static ArrayBlockingQueue<ByteBuffer> solvedMinutes = new ArrayBlockingQueue<>(64);
    public static int MAXBUFFERLEN=476824288;
    public static int DIRECT_CHUNCK_SIZE = MAXBUFFERLEN;
    public static int RAM_CHUNCK_SIZE = MAXBUFFERLEN*3;
    public static int Time_CHUNCK_SIZE = MAXBUFFERLEN;
    public static Thread[] smbbt=new Thread[16];
    public static int THREAD_NUMBER=7;

    PrepareMultiThreadManager(){
        Thread prepareThread=new Thread(()->{





            smt=new SplitMinuteThread(RAM_CHUNCK_SIZE,Time_CHUNCK_SIZE);
            smt.LinkBlockingQueue(canuse,canread);
            smt.start();
            System.out.println("smt启动");
            System.out.println("第二个directbuffer加载完成");
            for(int i=0;i<THREAD_NUMBER;++i){
                smbbt[i]=new SolveMinuteByteBufferThread(unsolvedMinutes,solvedMinutes);
                smbbt[i].start();

            }

            System.out.println("异步加载结束");
        });
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

            smt.join();
            smt.freeMemory();
            ByteBuffer b=ByteBuffer.allocate(1);
            b.limit(0);
            unsolvedMinutes.put(b);
            unsolvedMinutes.put(b);
            unsolvedMinutes.put(b);
            unsolvedMinutes.put(b);
            for(int i=0;i<THREAD_NUMBER;++i){
                smbbt[i].join();
            }
            while(!solvedMinutes.isEmpty()){
                solvedMinutes.poll();
            }
            SolveRespondThread.solve();
//            Analyse.findIpPair();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
