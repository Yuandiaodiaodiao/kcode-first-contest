package com.kuaishou.kcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.System.nanoTime;

public class DiskReadTest {
    public static void test(String path) {

        File f = new File(path);
        long fileLength = f.length();
        RandomAccessFile raf = null;
        long chunck = 200 * 1024 * 1024;
        try {
            raf = new RandomAccessFile(f, "r");
            FileChannel channel = raf.getChannel();

            ArrayBlockingQueue canuse = new ArrayBlockingQueue(16);
            ArrayBlockingQueue canread = new ArrayBlockingQueue(16);
            ByteBuffer buf=ByteBuffer.allocateDirect((int) chunck);

            canuse.add(buf);
            canuse.add(ByteBuffer.allocateDirect((int) chunck));
            canuse.add(ByteBuffer.allocateDirect((int) chunck));
            canuse.add(ByteBuffer.allocateDirect((int) chunck));
            canuse.add(ByteBuffer.allocateDirect((int) chunck));
            long startNs = nanoTime();
            Thread t1 = new Thread(() -> {
                try {
//                    byte[] buff=new byte[321*1024*1024];
                    while (true) {
                        ByteBuffer b = (ByteBuffer) canread.take();
                        if(b.limit()==0){canread.put(b);return;}

                        Byte by;
                        while(b.hasRemaining()){
//                            b.get(buff,0,b.remaining());
                            by=b.get();
                        }

                        canuse.put(b);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            Thread t2 = new Thread(() -> {
                try {
//                    byte[] buff=new byte[321*1024*1024];
                    while (true) {
                        ByteBuffer b = (ByteBuffer) canread.take();
                        if(b.limit()==0){canread.put(b);return;}

                        Byte by;
                        while(b.hasRemaining()){
//                            b.get(buff,0,b.remaining());
                            by=b.get();
                        }

                        canuse.put(b);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            Thread t3 = new Thread(() -> {
                try {
//                    byte[] buff=new byte[321*1024*1024];
                    while (true) {
                        ByteBuffer b = (ByteBuffer) canread.take();
                        if(b.limit()==0){canread.put(b);return;}
                        Byte by;
                        while(b.hasRemaining()){
//                            b.get(buff,0,b.remaining());
                            by=b.get();
                        }

                        canuse.put(b);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            Thread t4 = new Thread(() -> {
                try {
//                    byte[] buff=new byte[321*1024*1024];
                    while (true) {
                        ByteBuffer b = (ByteBuffer) canread.take();
                        if(b.limit()==0){canread.put(b);return;}
                        Byte by;
                        while(b.hasRemaining()){
//                            b.get(buff,0,b.remaining());
                            by=b.get();
                        }

                        canuse.put(b);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            for (long i = 0; i <= fileLength; i += chunck) {
                buf= (ByteBuffer) canuse.take();
                buf.clear();
                int readed = channel.read(buf);
                buf.flip();
                canread.put(buf);

            }
            buf= (ByteBuffer) canuse.take();
            buf.limit(0);
            canread.put(buf);
            canread.put(buf);
            canread.put(buf);
            canread.put(buf);
            canread.put(buf);
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            String s = ("磁盘耗时(ms):" + (nanoTime() - startNs) / 1000000.0);
            throw new ArrayIndexOutOfBoundsException(s + "文件长度" + fileLength / 1024.0 / 1024 + "MB");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
