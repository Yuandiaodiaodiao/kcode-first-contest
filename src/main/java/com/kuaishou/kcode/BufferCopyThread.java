package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class BufferCopyThread extends Thread {
    ArrayBlockingQueue<ByteBuffer> canuse;
    ArrayBlockingQueue<ByteBuffer> canread;
    ArrayBlockingQueue<BufferPayload> remaning;

    public  MultiByteBuffer buffer;

    BufferCopyThread() {
    }

    public void LinkBlockingQueue(ArrayBlockingQueue<ByteBuffer> canuse, ArrayBlockingQueue<ByteBuffer> canread, ArrayBlockingQueue<BufferPayload> remaning) {
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
                    remaning.offer(new BufferPayload());
                    return;
                }
                //直接拉满
                int remaining = b.remaining();
                //从directbuffer中抽出来
                //这个时候要wait 等对应位置buffer的线程完成工作之后 再写入
                if(remaining>buffer.canRead()){
                    //溢出了 要拆成两部分写入
                    int firstRead=buffer.canRead();
                    BufferPayload payload= new BufferPayload(buffer.position,firstRead,new MultiByteBuffer(buffer));
                    buffer.eatByteBuffer(b,firstRead);
                    remaning.offer(payload);
                    //通知buffer
                    buffer.nextOffset();
                    payload=new BufferPayload(0,b.remaining(),new MultiByteBuffer(buffer));
                    buffer.eatByteBuffer(b);
                    remaning.offer(payload);
                }else{
                    //可以直接读进去
                    BufferPayload payload=new BufferPayload(buffer.position,b.remaining(),new MultiByteBuffer(buffer));
                    buffer.eatByteBuffer(b);
                    remaning.offer(payload);
                }
                //读完了 把directbuffer放回
                canuse.offer(b);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
