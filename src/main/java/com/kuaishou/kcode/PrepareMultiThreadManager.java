package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class PrepareMultiThreadManager {
    String path;
    DiskReadThread drt;
    SplitMinuteThread smt;
    ArrayBlockingQueue<ByteBuffer> canuse = new ArrayBlockingQueue<>(16);
    ArrayBlockingQueue<ByteBuffer> canread = new ArrayBlockingQueue<>(16);
    public static ArrayBlockingQueue<ByteBuffer> unsolvedMinutes = new ArrayBlockingQueue<>(64);
    public static ArrayBlockingQueue<ByteBuffer> solvedMinutes = new ArrayBlockingQueue<>(64);
    public static int DIRECT_CHUNCK_SIZE = 500 * 1024 * 1024;
    public static int RAM_CHUNCK_SIZE = 2000 * 1024 * 1024;
    public static int Time_CHUNCK_SIZE = 500 * 1024 * 1024;
    public static Thread[] smbbt=new Thread[16];
    public static int THREAD_NUMBER=5;
    PrepareMultiThreadManager(){
        for(int a=0;a<THREAD_NUMBER+1;++a){
            solvedMinutes.add(ByteBuffer.allocate(Time_CHUNCK_SIZE));
        }
        canuse.add(ByteBuffer.allocateDirect(DIRECT_CHUNCK_SIZE));
        canuse.add(ByteBuffer.allocateDirect(DIRECT_CHUNCK_SIZE));
        drt=new DiskReadThread(DIRECT_CHUNCK_SIZE);
        drt.LinkBlockingQueue(canuse,canread);
        smt=new SplitMinuteThread(RAM_CHUNCK_SIZE,Time_CHUNCK_SIZE);
        smt.LinkBlockingQueue(canuse,canread);
        smt.start();
        for(int i=0;i<THREAD_NUMBER;++i){
            smbbt[i]=new SolveMinuteByteBufferThread(unsolvedMinutes,solvedMinutes);
            smbbt[i].start();
        }
    }
    public void setPath(String s){
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
            ByteBuffer b=ByteBuffer.allocate(1);
            b.limit(0);
            unsolvedMinutes.put(b);
            unsolvedMinutes.put(b);
            unsolvedMinutes.put(b);
            unsolvedMinutes.put(b);
            for(int i=0;i<THREAD_NUMBER;++i){
                smbbt[i].join();
            }
            SolveRespondThread.solve();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
