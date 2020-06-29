package com.kuaishou.kcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ArrayBlockingQueue;


public class DiskReadThread extends Thread {
    long fileLength;
    FileChannel channel;
    int CHUNCK_SIZE = 512 * 1024 * 1024;
    ArrayBlockingQueue<ByteBuffer> canuse;
    ArrayBlockingQueue<ByteBuffer> canread;

    DiskReadThread(int size)  {
       CHUNCK_SIZE=size;
    }
    public void setPath(String path) {
        File f = new File(path);
        fileLength = f.length();
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        channel = raf.getChannel();
    }
    public void LinkBlockingQueue(ArrayBlockingQueue<ByteBuffer> canuse,ArrayBlockingQueue<ByteBuffer> canread){
        this.canuse=canuse;
        this.canread=canread;
    }
    public static long DiskRead_waitBuffer=0;

    @Override
    public void run() {
        super.run();
        try {

            ByteBuffer buf = ByteBuffer.allocate(1);
            canuse.add(ByteBuffer.allocateDirect(PrepareMultiThreadManager.DIRECT_CHUNCK_SIZE));

            for (long i = 0; i <= fileLength; i += CHUNCK_SIZE) {

                if(canuse.size()==0){
                    System.out.println("DiskWait");
                }
                long t1=System.currentTimeMillis();
                buf = canuse.take();
                long t2=System.currentTimeMillis();
                DiskRead_waitBuffer+=(t2-t1);
//                System.out.println("DiskRead waitBuffer="+(t2-t1) +"ms");
                buf.clear();
                channel.read(buf);
                buf.flip();
                canread.offer(buf);

            }
            buf= ByteBuffer.allocate(1);
            buf.limit(0);
            //塞入读完的buff 让其他线程停止工作
            canread.put(buf);
            canread.put(buf);
            canread.put(buf);
            canread.put(buf);

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
