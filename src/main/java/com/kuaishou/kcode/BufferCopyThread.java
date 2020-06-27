package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class BufferCopyThread extends Thread {
    ArrayBlockingQueue<ByteBuffer> canuse;
    ArrayBlockingQueue<ByteBuffer> canread;
    ArrayBlockingQueue<Long> remaning;

    public  SuperByteBuffer buffer;

    BufferCopyThread() {
    }

    public void LinkBlockingQueue(ArrayBlockingQueue<ByteBuffer> canuse, ArrayBlockingQueue<ByteBuffer> canread, ArrayBlockingQueue<Long> remaning) {
        this.canuse = canuse;
        this.canread = canread;
        this.remaning = remaning;
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                ByteBuffer b = canread.take();
                if (b.limit() == 0) {
                    remaning.offer(-1L);
                    return;
                }
                //直接拉满
                int remaining = b.remaining();
                //从directbuffer中抽出来
                buffer.eatByteBuffer(b,remaining);
                remaning.offer((long) remaining);
                canuse.offer(b);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
